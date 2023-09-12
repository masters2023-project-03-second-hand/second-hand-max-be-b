package kr.codesquad.secondhand.application.image;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.util.ArrayList;
import kr.codesquad.secondhand.domain.image.ImageFile;
import kr.codesquad.secondhand.infrastructure.properties.AwsProperties;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class S3Uploader {

    private static final String PUBLIC_IMAGE_DIR = "public/";
    private static final int FILE_NAME_INDEX = 5;

    private final AmazonS3Client s3Client;
    private final String bucket;

    public S3Uploader(AmazonS3Client s3Client, AwsProperties awsProperties) {
        this.s3Client = s3Client;
        this.bucket = awsProperties.getS3().getBucket();
    }

    public String uploadImageFile(ImageFile imageFile) {
        final String fileName = PUBLIC_IMAGE_DIR + imageFile.getSavedFileName();
        putS3(imageFile, fileName);
        return getObjectUrl(fileName);
    }

    private String getObjectUrl(final String fileName) {
        return URLDecoder.decode(s3Client.getUrl(bucket, fileName).toString(), StandardCharsets.UTF_8);
    }

    public List<String> uploadImageFiles(List<ImageFile> imageFiles) {
        imageFiles.parallelStream()
                .forEach(imageFile -> putS3(imageFile, PUBLIC_IMAGE_DIR + imageFile.getSavedFileName()));

        return imageFiles.parallelStream()
                .map(imageFile -> getObjectUrl(PUBLIC_IMAGE_DIR + imageFile.getSavedFileName()))
                .collect(Collectors.toList());
    }

    private void putS3(ImageFile imageFile, final String fileName) {
        s3Client.putObject(createPutObjRequest(imageFile, fileName));
    }

    private PutObjectRequest createPutObjRequest(ImageFile imageFile, String fileName) {
        return new PutObjectRequest(
                bucket,
                fileName,
                imageFile.getInputStream(),
                createObjectMetaData(imageFile)
        ).withCannedAcl(CannedAccessControlList.PublicRead);
    }

    private ObjectMetadata createObjectMetaData(ImageFile imageFile) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());
        metadata.setContentLength(imageFile.getFileSize());
        return metadata;
    }

    public void deleteImage(String imageUrl) {
        String fileName = PUBLIC_IMAGE_DIR + imageUrl.split("/")[FILE_NAME_INDEX];
        s3Client.deleteObject(bucket, fileName);
    }

    public void deleteImages(List<String> imageUrls) {
        List<DeleteObjectsRequest.KeyVersion> keys = imageUrls.parallelStream()
                .map(imageUrl -> new KeyVersion(PUBLIC_IMAGE_DIR + imageUrl.split("/")[FILE_NAME_INDEX]))
                .collect(Collectors.toList());

        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket)
                .withKeys(keys)
                .withQuiet(false);
        s3Client.deleteObjects(deleteObjectsRequest);
    }
}
