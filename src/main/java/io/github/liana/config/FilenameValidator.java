package io.github.liana.config;

import org.apache.commons.io.FilenameUtils;
import java.util.Locale;
import static io.github.liana.config.FileExtensionValidator.defaultExtensions;

final class FilenameValidator {

    private static final String REGEX_SAFE_RESOURCE_NAME =
            "^(?!.*\\.\\.)[a-zA-Z0-9_\\-]+(?:/[a-zA-Z0-9_\\-]+)*\\.[a-zA-Z0-9]+$";

    public static boolean isSafeResourceName(String resourceName) {
        if (resourceName == null || resourceName.isBlank() ||
                resourceName.length() > 255 || resourceName.contains("%")) {
            return false;
        }

        String cleanResourceName = FilenameUtils.normalize(resourceName, true);
        if (cleanResourceName == null || cleanResourceName.startsWith("..") || cleanResourceName.contains("../")) {
            return false;
        }

        String fileName = FilenameUtils.getName(cleanResourceName);
        if (fileName.startsWith(".") || fileName.endsWith(".") || fileName.contains(" ")) {
            return false;
        }

        if (!cleanResourceName.matches(REGEX_SAFE_RESOURCE_NAME)) {
            return false;
        }

        String extension = FilenameUtils.getExtension(fileName).toLowerCase(Locale.ROOT);
        return defaultExtensions().contains(extension);
    }
}
