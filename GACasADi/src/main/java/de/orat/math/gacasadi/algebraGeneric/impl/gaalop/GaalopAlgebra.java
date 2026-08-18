package de.orat.math.gacasadi.algebraGeneric.impl.gaalop;

import de.gaalop.algebra.AlStrategy;
import de.gaalop.cfg.AlgebraDefinitionFile;
import de.gaalop.productComputer.GeoProductCalculator;
import de.gaalop.productComputer.InnerProductCalculator;
import de.gaalop.productComputer.OuterProductCalculator;
import de.gaalop.tba.Algebra;
import de.gaalop.tba.Blade;
import de.gaalop.tba.MultTableAbsDirectComputer;
import de.orat.math.gacasadi.algebraGeneric.api.IAlgebra;
import java.io.IOException;
import java.io.Reader;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GaalopAlgebra implements IAlgebra {

    // Some of those could be computed lazy only when used the first time.
    private final Algebra algebra;
    private final AlgebraDefinitionFile algebraDefinitionFile;
    public final Optional<Path> algebraLibFile;
    private final Path algebraPath;
    private final Product gp;
    private final Product inner;
    private final Product outer;
    private final List<Integer> gradeToConjugateSign;
    private final List<Integer> gradeToGradeInversionSign;
    private final List<Integer> gradeToReverseSign;
    /**
     * euclidBladeIndices and idleBladeIndices are disjoint and sum of number of elements are
     * getBladesCount()-1 (without 0-grade scalar).
     */
    private final List<Integer> euclidBladeIndices;
    /**
     * euclidBladeIndices and idleBladeIndices are disjoint and sum of number of elements are
     * getBladesCount()-1 (without 0-grade scalar).
     */
    private final List<Integer> idleBladeIndices;

    public GaalopAlgebra(String algebraName) {
        this.algebraPath = getAlgebraPath(algebraName);
        this.algebraDefinitionFile = getADF(this.algebraPath);
        this.algebra = getAlgebra(this.algebraDefinitionFile);
        this.algebraLibFile = getAlgebraLibFile(this.algebraPath);
        this.gp = new Product(new MultTableAbsDirectComputer(this.algebraDefinitionFile, new GeoProductCalculator()));
        this.inner = new Product(new MultTableAbsDirectComputer(this.algebraDefinitionFile, new InnerProductCalculator()));
        this.outer = new Product(new MultTableAbsDirectComputer(this.algebraDefinitionFile, new OuterProductCalculator()));
        final int gradesCount = 1 + this.algebra.getBaseCount();
        gradeToConjugateSign = Collections.unmodifiableList(IAlgebra.computeGradeToConjugateSign(gradesCount));
        gradeToGradeInversionSign = Collections.unmodifiableList(IAlgebra.computeGradeToGradeInversionSign(gradesCount));
        gradeToReverseSign = Collections.unmodifiableList(IAlgebra.computeGradeToReverseSign(gradesCount));

        var metric = GaalopAlgebra.getMetric(this.algebra, this.gp);
        var idleBaseIndices = GaalopAlgebra.getIdleBaseIndices(metric);
        var baseToIndex = GaalopAlgebra.getBaseToIndex(this.algebra);
        var bladeIndexToBaseIndices = GaalopAlgebra.getBladeIndexToBaseIndices(this.algebra, baseToIndex);
        this.euclidBladeIndices = Collections.unmodifiableList(GaalopAlgebra.getEuclidBladeIndices(idleBaseIndices, bladeIndexToBaseIndices));
        this.idleBladeIndices = Collections.unmodifiableList(GaalopAlgebra.getIdleBladeIndices(idleBaseIndices, bladeIndexToBaseIndices));
    }

    @Override
    public Product gp() {
        return this.gp;
    }

    @Override
    public Product inner() {
        return this.inner;
    }

    @Override
    public Product outer() {
        return this.outer;
    }

    private static final Path algebrasDir = resolveAlgebrasDir();

    public static Path resolveAlgebrasDir() {
        try {
            String dir = "/de/gaalop/algebra/algebra/";
            URL url = de.orat.math.gacalcdata.util.tbastandalone.Main.class.getResource(dir);
            Path path = switch (url.getProtocol()) {
                case "jar" -> {
                    JarURLConnection connection = (JarURLConnection) url.openConnection();
                    var jarFileUri = connection.getJarFileURL().toURI();
                    var fs = FileSystems.newFileSystem(Paths.get(jarFileUri));
                    yield fs.getPath(dir);
                }
                default ->
                    Path.of(url.toURI());
            };
            return path;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Path getAlgebraPath(String algebraName) {
        Path algebraPath = algebrasDir.resolve(algebraName);
        if (Files.notExists(algebraPath)) {
            throw new IllegalArgumentException(String.format("%s does not exist.", algebraPath));
        }
        return algebraPath;
    }

    public static Optional<Path> getAlgebraLibFile(Path algebraPath) {
        Path libFile = algebraPath.resolve("lib.ocga");
        if (Files.notExists(libFile)) {
            return Optional.empty();
        }
        return Optional.of(libFile);
    }

    public static AlgebraDefinitionFile getADF(Path algebraPath) {
        Path definitionFilePath = algebraPath.resolve("definition.csv");
        if (Files.notExists(definitionFilePath)) {
            throw new IllegalArgumentException(String.format("%s does not exist.", definitionFilePath));
        }
        try {
            Reader definitionFileReader = Files.newBufferedReader(definitionFilePath);
            AlgebraDefinitionFile adf = new AlgebraDefinitionFile();
            adf.loadFromFile(definitionFileReader);
            AlStrategy.createBlades(adf);
            return adf;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Algebra getAlgebra(AlgebraDefinitionFile adf) {
        Algebra algebra = new Algebra(adf);
        return algebra;
    }

    @Override
    public int getBaseSize() {
        return this.algebra.getBaseCount();
    }

    @Override
    public List<String> bladeOfBasevectorsFromIndex(int index) {
        Blade blade = this.algebra.getBlade(index);
        return blade.getBases();
    }

    @Override
    public int indexOfBlade(String baseVector) {
        Blade blade = new Blade(List.of(baseVector));
        int index;
        try {
            index = this.algebra.getIndex(blade);
        } catch (NullPointerException npe) {
            throw new IllegalArgumentException(String.format("blade \"%s\" not found.", baseVector), npe);
        }
        return index;
    }

    @Override
    public int indexOfBlade(String... bladeOfBasevectors) {
        Blade blade = new Blade(bladeOfBasevectors);
        int index;
        try {
            index = this.algebra.getIndex(blade);
        } catch (NullPointerException npe) {
            throw new IllegalArgumentException(String.format("blade \"%s\" not found.", Arrays.toString(bladeOfBasevectors)), npe);
        }
        return index;
    }

    @Override
    public int[] getIndizes(int grade) {
        return algebra.getIndizes(grade);
    }

    @Override
    // ungetested
    public int[] getIndizes(int[] grades) {
        List<Integer> indizes = new ArrayList<>();
        for (int i = 0; i < grades.length; i++) {
            int[] ints = getIndizes(grades[i]);
            indizes.addAll(Arrays.stream(ints).boxed().toList());
        }
        // stream() converts given ArrayList to stream
        // mapToInt() converts the obtained stream to IntStream
        // toArray() is used to return an array
        return indizes.stream().mapToInt(Integer::intValue).toArray();
    }

    // Always same for same Algebra. Could be cached.
    @Override
    public int[] getEvenIndizes() {
        return getIndizes(getEvenGrades());
    }

    // Always same for same Algebra. Could be cached.
    @Override
    public int[] getEvenGrades() {
        final int gradesCount = this.getGradesCount();
        List<Integer> evenGrades = new ArrayList<>(gradesCount);
        for (int i = 0; i < gradesCount; i += 2) {
            evenGrades.add(i);
        }
        return evenGrades.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public List<Integer> getGrades(List<Integer> indices) {
        return indices.stream()
            .map(i -> this.algebra.getBlade(i).getBases().size())
            .distinct()
            .sorted()
            .toList();
    }

    @Override
    public int getGrade(int index) {
        return algebra.getGrade(index);
    }

    @Override
    public int gradeToConjugateSign(int grade) {
        return gradeToConjugateSign.get(grade);
    }

    @Override
    public int gradeToGradeInversionSign(int grade) {
        return gradeToGradeInversionSign.get(grade);
    }

    @Override
    public int gradeToReverseSign(int grade) {
        return gradeToReverseSign.get(grade);
    }

    @Override
    public List<Integer> getEuclidBladeIndices() {
        return this.euclidBladeIndices;
    }

    @Override
    public List<Integer> getIdleBladeIndices() {
        return this.idleBladeIndices;
    }

    /**
     * Base without scalar. Indices start with 1.
     */
    private static Map<String, Integer> getBaseToIndex(de.gaalop.tba.Algebra algebra) {
        int[] oneBladeIndices = algebra.getIndizes(1);
        Map<String, Integer> baseToIndex = LinkedHashMap.newLinkedHashMap(oneBladeIndices.length);
        for (int index : oneBladeIndices) {
            String bladeBase = algebra.getBlade(index).getBases().get(0); // 1-grade: Exactly 1 base.
            baseToIndex.put(bladeBase, index);
        }
        return baseToIndex;
    }

    /**
     * Base without scalar. At blade index 0 is the empty set.
     */
    private static List<Set<Integer>> getBladeIndexToBaseIndices(de.gaalop.tba.Algebra algebra, Map<String, Integer> baseToIndex) {
        final int bladesCount = algebra.getBladeCount();
        List<Set<Integer>> bladeIndexToBladeBaseIndices = new ArrayList<>(bladesCount);
        // Ignore blade 0. 0-grade is nor euclid nor idle.
        bladeIndexToBladeBaseIndices.add(Collections.emptySet());
        // Ignore blade 0. 0-grade is nor euclid nor idle.
        for (int bladeIndex = 1; bladeIndex < bladesCount; ++bladeIndex) {
            Blade blade = algebra.getBlade(bladeIndex);
            Set<Integer> bladeBaseIndices = blade.getBases().stream()
                .map(baseToIndex::get)
                .collect(Collectors.toCollection(HashSet::new));
            bladeIndexToBladeBaseIndices.add(bladeBaseIndices);
        }

        return bladeIndexToBladeBaseIndices;
    }

    /**
     * Base without scalar.
     */
    private static List<Float> getMetric(de.gaalop.tba.Algebra algebra, Product gp) {
        // Blade indices of grade 1 are the indices of the base.
        int[] oneBladeIndices = algebra.getIndizes(1);
        List<Float> metric = new ArrayList<>(oneBladeIndices.length);
        for (int oneBladeIndex : oneBladeIndices) {
            var gpMvEntries = gp.product(oneBladeIndex, oneBladeIndex).entries();
            final int entriesSize = gpMvEntries.size();
            // Squares to 0.
            if (entriesSize == 0) {
                metric.add(0f);
                continue;
            }
            if (entriesSize != 1) {
                throw new RuntimeException();
            }
            float coefficient = gpMvEntries.get(0).coefficient();
            metric.add(coefficient);
        }
        return metric;
    }

    /**
     * Base without scalar. Starts with 1.
     */
    private static List<Integer> getIdleBaseIndices(List<Float> metric) {
        final float epsilon = 1e-3f; // Will probably optimized by the JIT.
        List<Integer> metricIndices = IntStream.range(1, metric.size()).boxed().toList();
        List<Integer> idleIndices = new ArrayList<>(metric.size()); // Maximum
        for (Integer metricIndex : metricIndices) {
            final float metricValue = metric.get(metricIndex - 1);
            if (checkEpsilon(metricValue, +1f, epsilon)) {
                // euclid()
                continue;
            } else if (checkEpsilon(metricValue, -1f, epsilon)) {
                idleIndices.add(metricIndex);
                continue;
            } else if (checkEpsilon(metricValue, 0f, epsilon)) {
                idleIndices.add(metricIndex);
                continue;
            } else {
                throw new RuntimeException();
            }
        }
        return idleIndices;
    }

    private static boolean checkEpsilon(float actualValue, float target, float epsilon) {
        return Math.abs(actualValue - target) <= epsilon;
    }

    private static List<Integer> getEuclidBladeIndices(List<Integer> idleBaseIndices, List<Set<Integer>> bladeIndexToBaseIndices) {
        // Euclid: Does not contain idleBaseIndices nor 0-grade.
        // 0-grade scalar has index 0, thus start with 1.
        List<Integer> euclidBladeIndices = IntStream.range(1, bladeIndexToBaseIndices.size())
            .boxed()
            .filter(bladeIndex -> Collections.disjoint(bladeIndexToBaseIndices.get(bladeIndex), idleBaseIndices)) // Contains no idle.
            .toList();

        return euclidBladeIndices;
    }

    private static List<Integer> getIdleBladeIndices(List<Integer> idleBaseIndices, List<Set<Integer>> bladeIndexToBaseIndices) {
        // Idle: Does  contain idleBaseIndices, but not 0-grade.
        // 0-grade scalar has index 0, thus start with 1.
        List<Integer> idleBladeIndices = IntStream.range(1, bladeIndexToBaseIndices.size())
            .boxed()
            .filter(bladeIndex -> !Collections.disjoint(bladeIndexToBaseIndices.get(bladeIndex), idleBaseIndices)) // Contains at least one idle.
            .toList();

        return idleBladeIndices;
    }
}
