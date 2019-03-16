/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xcleans.common.logger;

/**
 * Log level
 *
 * @author fengwx
 */
public final class Level {

    /**
     * Lowest level, turn on all logging
     */
    public static final int ALL = 1;

    /**
     * Priority constant for the println method; use Logger.v().
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Logger.d().
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Logger.i().
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Logger.w().
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Logger.e().
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;

    /**
     * Highest level, turn off loading
     */
    public static final int OFF = 8;

}
