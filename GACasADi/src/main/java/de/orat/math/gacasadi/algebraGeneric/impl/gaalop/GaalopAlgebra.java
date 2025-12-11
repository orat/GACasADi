package de.orat.math.gacasadi.algebraGeneric.impl.gaalop;

import de.gaalop.algebra.AlStrategy;
import de.gaalop.cfg.AlgebraDefinitionFile;
import de.gaalop.productComputer.GeoProductCalculator;
import de.gaalop.productComputer.InnerProductCalculator;
import de.gaalop.productComputer.OuterProductCalculator;
import de.gaalop.tba.Algebra;
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

public class GaalopAlgebra implements IAlgebra {

    public final Algebra algebra;
    public final AlgebraDefinitionFile algebraDefinitionFile;

    public GaalopAlgebra(String algebraName) {
        Path algebraPath = getAlgebraPath(algebraName);
        this.algebraDefinitionFile = getADF(algebraPath);
        this.algebra = getAlgebra(this.algebraDefinitionFile);
    }

    @Override
    public Product gp() {
        var comp = new MultTableAbsDirectComputer(this.algebraDefinitionFile, new GeoProductCalculator());
        return new Product(comp);
    }

    @Override
    public Product inner() {
        var comp = new MultTableAbsDirectComputer(this.algebraDefinitionFile, new InnerProductCalculator());
        return new Product(comp);
    }

    @Override
    public Product outer() {
        var comp = new MultTableAbsDirectComputer(this.algebraDefinitionFile, new OuterProductCalculator());
        return new Product(comp);
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
}
