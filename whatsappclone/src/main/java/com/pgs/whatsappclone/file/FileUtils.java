package com.pgs.whatsappclone.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

	private FileUtils() {
		
	}
	
	/**
	 * Reads the content of a file located at the given path and returns it as a byte array.
	 * <p>
	 * If the provided file path is blank or the file cannot be read, an empty byte array is returned.
	 * </p>
	 *
	 * @param fileUrl the full path of the file to read
	 * @return a byte array containing the file's content, or an empty byte array if the path is blank or reading fails
	 */
	public static byte[] readFileFromLocation(String fileUrl) {
	    if (StringUtils.isBlank(fileUrl)) {
	        log.warn("Provided file path is blank or null.");
	        return new byte[0];
	    }
	    
	    try {
	        Path file = new File(fileUrl).toPath();
	        byte[] fileContent = Files.readAllBytes(file);
	        log.info("File read successfully from path: {}", fileUrl);
	        return fileContent;
	    } catch (IOException e) {
	        log.warn("Failed to read file from path {}: {}", fileUrl, e.getMessage());
	    }
	    
	    return new byte[0];
	}
}
