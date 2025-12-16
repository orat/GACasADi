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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GaalopAlgebra implements IAlgebra {

    private final Algebra algebra;
    private final AlgebraDefinitionFile algebraDefinitionFile;
    public final Optional<Path> algebraLibFile;
    private final Path algebraPath;
    private final Product gp;
    private final Product inner;
    private final Product outer;

    public GaalopAlgebra(String algebraName) {
        this.algebraPath = getAlgebraPath(algebraName);
        this.algebraDefinitionFile = getADF(this.algebraPath);
        this.algebra = getAlgebra(this.algebraDefinitionFile);
        this.algebraLibFile = getAlgebraLibFile(this.algebraPath);
        this.gp = new Product(new MultTableAbsDirectComputer(this.algebraDefinitionFile, new GeoProductCalculator()));
        this.inner = new Product(new MultTableAbsDirectComputer(this.algebraDefinitionFile, new InnerProductCalculator()));
        this.outer = new Product(new MultTableAbsDirectComputer(this.algebraDefinitionFile, new OuterProductCalculator()));
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
}
