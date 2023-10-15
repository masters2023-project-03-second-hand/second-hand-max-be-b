package kr.codesquad.secondhand.application.image;

import java.util.List;
import java.util.stream.Collectors;
import kr.codesquad.secondhand.domain.image.ImageFile;
import kr.codesquad.secondhand.domain.itemimage.ItemImage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
        if (images == null) {
            return List.of();
        }
        List<ImageFile> imageFiles = images.stream()
                .map(ImageFile::from)
                .collect(Collectors.toList());
        return s3Uploader.uploadImageFiles(imageFiles);
    }

    @Async("imageThreadExecutor")
    public void deleteImages(List<ItemImage> itemImages) {
        List<String> imageUrls = itemImages.parallelStream()
                .map(ItemImage::getImageUrl)
                .collect(Collectors.toList());
        s3Uploader.deleteImages(imageUrls);
    }
}
