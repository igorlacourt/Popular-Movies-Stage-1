/*
 * Copyright (C) 2016 Piotr Wittchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.udacity.lacourt.popularmoviesstage1.utils;

/* THIS CLASS IS NOT MADE BY ME. I TOOK FROM SOME TUTORIAL ON INTERNET. JUST DON'T REMEMBER WHERE :\ .
     I READ UDACITY'S WARNNING ABOUT THIRD PARTIES CODE JUST WHEN I WAS ABOUT TO SUBMIT THIS PROJECT
*/

public class Preconditions {
  public static void checkNotNull(Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void checkIfPositive(int number, String message) {
    if (number <= 0) {
      throw new IllegalArgumentException(message);
    }
  }
}
