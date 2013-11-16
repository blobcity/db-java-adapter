
package com.blobcity.db.classannotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Karishma
 * @author Sanket Sarang <sanket@blobcity.net>
 * @since 1.0
 * @version 1.1
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    public String table();
}
