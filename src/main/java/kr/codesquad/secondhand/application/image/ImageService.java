package kr.codesquad.secondhand.application.image;

import kr.codesquad.secondhand.domain.image.ImageFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ImageService {

    private final S3Uploader s3Uploader;

    @Transactional
    public String uploadImage(MultipartFile image) {
        ImageFile imageFile = ImageFile.from(image);
        return s3Uploader.uploadImageFile(imageFile);
    }

    @Transactional
    public List<String> uploadImages(List<MultipartFile> images) {
        List<ImageFile> imageFiles = images.stream()
                .map(ImageFile::from)
                .collect(Collectors.toList());
        return s3Uploader.uploadImageFiles(imageFiles);
    }
}
