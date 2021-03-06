/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.hadoop;

import com.google.common.base.Throwables;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility methods for the Hadoop client tests.
 */
public final class HadoopClientTestUtils {
  /**
   * Resets the initialized flag in {@link AbstractFileSystem} allowing FileSystems with
   * different URIs to be initialized.
   */
  public static void resetHadoopClientContext() {
    try {
      Whitebox.setInternalState(AbstractFileSystem.class, "sInitialized", false);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  public static boolean isHadoop1x() {
    return getHadoopVersion().startsWith("1");
  }

  public static boolean isHadoop2x() {
    return getHadoopVersion().startsWith("2");
  }

  public static String getHadoopVersion() {
    try {
      final URL url = getSourcePath(org.apache.hadoop.fs.FileSystem.class);
      final File path = new File(url.toURI());
      final String[] splits = path.getName().split("-");
      final String last = splits[splits.length - 1];
      return last.substring(0, last.lastIndexOf("."));
    } catch (URISyntaxException e) {
      throw new AssertionError(e);
    }
  }

  private static URL getSourcePath(Class<?> clazz) {
    try {
      clazz = getClassLoader(clazz).loadClass(clazz.getName());
      return clazz.getProtectionDomain().getCodeSource().getLocation();
    } catch (ClassNotFoundException e) {
      throw new AssertionError("Unable to find class " + clazz.getName());
    }
  }

  private static ClassLoader getClassLoader(Class<?> clazz) {
    // Power Mock makes this hard, so try to hack it
    ClassLoader cl = clazz.getClassLoader();
    if (cl instanceof MockClassLoader) {
      cl = cl.getParent();
    }
    return cl;
  }
}
