package com.sila.share.annotation;

import com.sila.share.enums.ROLE;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreAuthorization {
  ROLE[] value();
}
