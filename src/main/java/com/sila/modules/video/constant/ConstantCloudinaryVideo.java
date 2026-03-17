package com.sila.modules.video.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConstantCloudinaryVideo {

  public static final String RESOURCE_TYPE = "resource_type";
  public static final String RESOURCE_VIDEO = "video";

  public static final String PUBLIC_ID = "public_id";
  public static final String CHUNK_SIZE = "chunk_size";

  public static final Number CHUNK_MB = 6000000;
  public static final String FORMAT_MP4 = "mp4";
}
