package com.sila.share.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StaticMessage {

  //  GENERAL
  public static final String RESOURCE_NOT_FOUND = "resource not found";
  public static final String COURSE_NOT_FOUND = "Course not found";

  //  VIDEO
  public static final String VIDEO_UPLOAD_FAILED = "Video upload failed: ";
  public static final String VIDEO_DELETE_FAILED = "Video delete failed: ";
  public static final String BULK_VIDEO_FAILED = "Bulk video delete failed: ";

  //   COURSE
  public static final String COURSE_HAS_ENROLLMENT =
      "Cannot delete course because students already enrolled";

  //  ENROLLMENT

  public static final String DELETE_BULK_ENROLLMENT_BY_COURSE =
      "success to delete bulk enrollment by course";
}
