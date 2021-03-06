package ua.antonsher.mypictool.actions;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.antonsher.mypictool.TiledImageBuilder;
import ua.antonsher.mypictool.filewriter.ImageFileWriter;
import ua.antonsher.mypictool.layout.LayoutUtil;
import ua.antonsher.mypictool.util.Conversions;
import ua.antonsher.mypictool.util.DateTimeUtil;

/**
 * Place multiple copies of an image on the white canvas, tiling it evenly.
 */
public class CreateTiledImageImpl implements CreateTiledImage {
    private static final Logger logger = LoggerFactory.getLogger(CreateTiledImageImpl.class);
    private static final int DPI = 300;
    private static final Dimension DIMENSION_35x45_MM = new Dimension(35, 45);
    private static final Dimension DIMENSION_10x15_CM = new Dimension(150, 100);
    private static final String FILE_EXTENSION_REGEX = "(\\.[^.]+)$";
    private static final String FILE_NAME_ENDING = "-10x15.jpg";
    
	private ImageFileWriter writer;
    
    public @Inject CreateTiledImageImpl(ImageFileWriter writer) {
		this.writer = writer;
    }
    
    @Override
	public void createTiledImage(File file) throws IOException {
        logger.debug("Reading file {}", file);

        final BufferedImage input = ImageIO.read(file);
        final Dimension canvasDimension = Conversions.mmToPixel(DIMENSION_10x15_CM, DPI);
        final Dimension tileDimension = Conversions.mmToPixel(DIMENSION_35x45_MM, DPI);
        logger.debug("Dimensions of canvas: {}, tile: {}", canvasDimension, tileDimension);

        final String headerCaption = DateTimeUtil.currentTimeCaption();
        logger.debug("Building tiled image with caption '{}'", headerCaption);
        final TiledImageBuilder imageBuilder = new TiledImageBuilder(new LayoutUtil(), canvasDimension, headerCaption, DPI);
        final BufferedImage image = imageBuilder.build(input, tileDimension);

        final String outputFileName = file.getName().replaceAll(FILE_EXTENSION_REGEX, "") + FILE_NAME_ENDING;
        final File outputFile = new File(outputFileName);

        logger.info("Writing output file {}", outputFile);
        writer.saveAsJpeg(image, outputFile, DPI);
    }
}
