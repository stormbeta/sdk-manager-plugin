package com.jakewharton.sdkmanager.internal

import org.apache.commons.io.FileUtils
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.rauschig.jarchivelib.ArchiverFactory

import static org.rauschig.jarchivelib.ArchiveFormat.TAR
import static org.rauschig.jarchivelib.ArchiveFormat.ZIP
import static org.rauschig.jarchivelib.CompressionType.GZIP


/** Manages platform-specific SDK downloads. */
class SdkDownload implements Downloader {
  final String baseUrl
  final String sdkMajorVersion
  final Logger log = Logging.getLogger SdkDownload
  final SdkPlatform platform

  private String getExt() { return platform.ext }
  private String getSuffix() { return platform.suffix }

  SdkDownload(platform = SdkPlatform.get(), baseUrl = SdkResolver.DEFAULT_SDK_URL, sdkVersion = SdkResolver.DEFAULT_SDK_VERSION) {
    this.platform = platform
    this.baseUrl = baseUrl
    this.sdkMajorVersion = sdkVersion
  }

  /** Download the SDK to {@code temp} and extract to {@code dest}. */
  void download(File dest) {
    def url = "${baseUrl}/android-sdk_r${sdkMajorVersion}-${suffix}.${ext}"
    log.debug "Downloading SDK from $url."

    File temp = new File(dest.getParentFile(), 'android-sdk.temp')
    temp.withOutputStream {
      it << new URL(url).content
    }

    // Archives have a single child folder. Extract to the parent directory.
    def parentFile = temp.getParentFile()
    log.debug "Extracting SDK to $parentFile.absolutePath."
    getArchiver().extract(temp, parentFile)

    // Move the aforementioned child folder to the real destination.
    def extracted = new File(parentFile, "android-sdk-$suffix")
    FileUtils.moveDirectory extracted, dest

    // Delete downloaded archive.
    temp.delete()
  }

  def getArchiver() {
    switch (ext) {
      case 'zip':
        return ArchiverFactory.createArchiver(ZIP)
      case 'tgz':
        return ArchiverFactory.createArchiver(TAR, GZIP)
      default:
        throw new IllegalArgumentException("Unknown archive format '$ext'.")
    }
  }
}
