package io.github.liana.config;

import io.github.liana.internal.FilenameUtils;
import io.github.liana.internal.StringUtils;
import java.util.Locale;

final class FilenameValidator {

  public static boolean isSafeResourceName(String resourceName) {
    if (StringUtils.isBlank(resourceName)) {
      return false;
    }

    String cleanResourceName = FilenameUtils.normalize(resourceName);
    if (cleanResourceName.startsWith("..") || cleanResourceName.contains("../")) {
      return false;
    }

    String fileName = FilenameUtils.getName(cleanResourceName);

    String extension = FilenameUtils.getExtension(fileName).toLowerCase(Locale.ROOT);
    return ConfigFileFormat.getAllSupportedExtensions().contains(extension);
  }
}
