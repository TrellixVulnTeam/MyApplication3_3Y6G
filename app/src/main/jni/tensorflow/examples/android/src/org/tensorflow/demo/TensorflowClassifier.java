/* Copyright 2015 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package tensorflow.examples.android.src.org.tensorflow.demo;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * JNI wrapper class for the Tensorflow native code.
 */
public class TensorflowClassifier implements Classifier {
  private static final String TAG = "TensorflowClassifier";

  // jni native methods.
  public native int initializeTensorflow(
      AssetManager assetManager,
      String model,
      String labels,
      int numClasses,
      int inputSize,
      int imageMean);

  private native String classifyImageBmp(Bitmap bitmap);

  private native String classifyImageRgb(int[] output, int width, int height);

  static {
    System.loadLibrary("tensorflow_demo");
  }

  @Override
  public List<Recognition> recognizeImage(final Bitmap bitmap) {
    final ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
    for (final String result : classifyImageBmp(bitmap).split("\n")) {
      Log.i(TAG, "Parsing [" + result + "]");

      // Clean up the string as needed
      final StringTokenizer st = new StringTokenizer(result);
      if (!st.hasMoreTokens()) {
        continue;
      }

      final String id = st.nextToken();
      final String confidenceString = st.nextToken();
      final float confidence = Float.parseFloat(confidenceString);

      final String title =
          result.substring(id.length() + confidenceString.length() + 2, result.length());

      if (!title.isEmpty()) {
        recognitions.add(new Recognition(id, title, confidence, null));
      }
    }
    return recognitions;
  }

  @Override
  public void close() {}
}
