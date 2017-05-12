/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.bootstrap;

import java.nio.file.Path;

import org.apache.cassandra.utils.NativeLibrary;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

/**
 * The Natives class is a wrapper class that checks if the classes necessary for calling native methods are available on
 * startup. If they are not available, this class will avoid calling code that loads these classes.
 */
final class Natives {
    /** no instantiation */
    private Natives() {}

    private static final ESLogger logger = Loggers.getLogger(Natives.class);

    // marker to determine if the JNA class files are available to the JVM
    /*
    static final boolean JNA_AVAILABLE;

    static {
        boolean v = false;
        try {
            // load one of the main JNA classes to see if the classes are available. this does not ensure that all native
            // libraries are available, only the ones necessary by JNA to function
            Class.forName("com.sun.jna.Native");
            v = true;
        } catch (ClassNotFoundException e) {
            logger.warn("JNA not found. native methods will be disabled.", e);
        } catch (UnsatisfiedLinkError e) {
            logger.warn("unable to load JNA native support library, native methods will be disabled.", e);
        }
        JNA_AVAILABLE = v;
    }
    */
    
    static void tryMlockall() {
        logger.info("NativeLibrary loaded by cassandra, nativeAvailable="+NativeLibrary.isAvailable()+" jnaMemoryLockable="+NativeLibrary.jnaMemoryLockable());
        /*
         * Done by cassandra.
        if (!JNA_AVAILABLE) {
            logger.warn("cannot mlockall because JNA is not available");
            return;
        }
        CLibrary.tryMlockall();
        */
    }

    static boolean definitelyRunningAsRoot() {
        if (!NativeLibrary.isAvailable()) {
            logger.warn("cannot check if running as root because JNA is not available");
            return false;
        }
        return JNANatives.definitelyRunningAsRoot();
    }
 
    static void tryVirtualLock() {
        if (!NativeLibrary.isAvailable()) {
            logger.warn("cannot mlockall because JNA is not available");
            return;
        }
        JNANatives.tryVirtualLock();
    }

    static void addConsoleCtrlHandler(ConsoleCtrlHandler handler) {
        if (!NativeLibrary.isAvailable()) {
            logger.warn("cannot register console handler because JNA is not available");
            return;
        }
        JNANatives.addConsoleCtrlHandler(handler);
    }

    static boolean isMemoryLocked() {
        return NativeLibrary.jnaMemoryLockable();
    }
    
    static void trySeccomp(Path tmpFile) {
        if (!NativeLibrary.isAvailable()) {
            logger.warn("cannot install syscall filters because JNA is not available");
            return;
        }
        JNANatives.trySeccomp(tmpFile);
    }
    
    static boolean isSeccompInstalled() {
        if (!NativeLibrary.isAvailable()) {
            return false;
        }
        return JNANatives.LOCAL_SECCOMP;
    }
}
