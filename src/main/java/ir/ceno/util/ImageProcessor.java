package ir.ceno.util;

import ir.ceno.exception.ImageAspectRatioException;
import ir.ceno.exception.ImageSizeException;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

// import org.imgscalr.Scalr;

@Component
public class ImageProcessor {

    /*public byte[] validateAndResizeImage(MultipartFile image, int minHeight, int minWidth,
                                         double expectedAspectRatio) throws IOException {


        BufferedImage input = ImageIO.read(image.getInputStream());
        validateWidthAndHeight(input, minWidth, minHeight);
        validateAspectRatio(input, expectedAspectRatio);

        BufferedImage output = Scalr.resize(input, minWidth, minHeight);
        // close or flush is not needed
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String subtype = extractSubtype(image.getContentType());
        ImageIO.write(output, subtype, outputStream);
        return outputStream.toByteArray();
    }*/

    private void validateWidthAndHeight(BufferedImage image, int minWidth, int minHeight) {
        if (image.getWidth() < minWidth || image.getHeight() < minHeight) {
            throw new ImageSizeException();
        }
    }

    private void validateAspectRatio(BufferedImage image, double expectedAspectRatio) {
        double imageAspectRatio = 1.0 * image.getWidth() / image.getHeight();
        if (imageAspectRatio != expectedAspectRatio) {
            throw new ImageAspectRatioException();
        }
    }

    private String extractSubtype(String mimeType) {
        int subtypeStart = mimeType.indexOf('/') + 1;
        return mimeType.substring(subtypeStart, mimeType.length());
    }
}
