package com.pgs.whatsappclone.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
	
	@Value("{application.file.uploads.media-output-path}")
	private String fileUploadPath;
	
	public String saveFile(
			@NonNull MultipartFile sourceFile, 
			@NonNull String userId) {
		
		final String fileUploadSubPath = "users" + File.separator + userId;
		return uploadFile(sourceFile, fileUploadSubPath);		
	}

	/**
	 * Uploads a file to a specified subdirectory within the configured upload path.
	 * <p>
	 * If the target subdirectory does not exist, it attempts to create it.
	 * The uploaded file is saved with a unique name based on the current timestamp,
	 * preserving the original file extension.
	 * </p>
	 *
	 * @param sourceFile        the multipart file to upload; must not be null
	 * @param fileUploadSubPath the relative subfolder path under the base upload directory; must not be null
	 * @return the absolute path of the saved file if successful, or {@code null} if the operation failed
	 */
	private String uploadFile(
	        @NonNull MultipartFile sourceFile,
	        @NonNull String fileUploadSubPath) {

	    final String finalUploadPath = this.fileUploadPath + File.separator + fileUploadSubPath;
	    File targetFolder = new File(finalUploadPath);

	    log.debug("Attempting to upload file: {}, to directory: {}", sourceFile.getOriginalFilename(), finalUploadPath);

	    if (!targetFolder.exists()) {
	        log.debug("Target folder does not exist. Attempting to create: {}", finalUploadPath);
	        boolean folderCreated = targetFolder.mkdirs();
	        if (!folderCreated) {
	            log.warn("Failed to create the target folder: {}", targetFolder.getAbsolutePath());
	            return null;
	        } else {
	            log.debug("Target folder created successfully: {}", targetFolder.getAbsolutePath());
	        }
	    }

	    final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
	    final String targetFileName = System.currentTimeMillis() + fileExtension;
	    final String targetFilePath = finalUploadPath + File.separator + targetFileName;
	    Path targetPath = Paths.get(targetFilePath);

	    try {
	        Files.write(targetPath, sourceFile.getBytes());
	        log.info("File saved successfully to {}", targetPath);
	        return targetFilePath;
	    } catch (IOException e) {
	        log.error("Failed to save file to {}", targetPath, e);
	    }

	    return null;
	}

	/**
	 * Extracts the file extension from the given filename.
	 * <p>
	 * If the filename is null, empty, or does not contain a dot, an empty string is returned.
	 * The returned extension is converted to lowercase.
	 * </p>
	 *
	 * @param fileName the name of the file (e.g., "document.pdf")
	 * @return the file extension (e.g., "pdf") or an empty string if not present
	 */
	private String getFileExtension(String fileName) {
	    if (fileName == null || fileName.isEmpty()) {
	        return "";
	    }

	    int lastDotIndex = fileName.lastIndexOf(".");
	    if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
	        return "";
	    }

	    return fileName.substring(lastDotIndex + 1).toLowerCase();
	}

}
