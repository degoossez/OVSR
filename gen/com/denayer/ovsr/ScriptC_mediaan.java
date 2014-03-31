/*
 * Copyright (C) 2011-2013 The Android Open Source Project
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

/*
 * This file is auto-generated. DO NOT MODIFY!
 * The source Renderscript file: /home/koen/git/OVSR/src/com/denayer/ovsr/mediaan.rs
 */
package com.denayer.ovsr;

import android.renderscript.*;
import android.content.res.Resources;

/**
 * @hide
 */
public class ScriptC_mediaan extends ScriptC {
    private static final String __rs_resource_name = "mediaan";
    // Constructor
    public  ScriptC_mediaan(RenderScript rs) {
        this(rs,
             rs.getApplicationContext().getResources(),
             rs.getApplicationContext().getResources().getIdentifier(
                 __rs_resource_name, "raw",
                 rs.getApplicationContext().getPackageName()));
    }

    public  ScriptC_mediaan(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        __U8_4 = Element.U8_4(rs);
    }

    private Element __U8_4;
    private FieldPacker __rs_fp_ALLOCATION;
    private FieldPacker __rs_fp_I32;
    private FieldPacker __rs_fp_SCRIPT;
    private final static int mExportVarIdx_out = 0;
    private Allocation mExportVar_out;
    public synchronized void set_out(Allocation v) {
        setVar(mExportVarIdx_out, v);
        mExportVar_out = v;
    }

    public Allocation get_out() {
        return mExportVar_out;
    }

    private final static int mExportVarIdx_in = 1;
    private Allocation mExportVar_in;
    public synchronized void set_in(Allocation v) {
        setVar(mExportVarIdx_in, v);
        mExportVar_in = v;
    }

    public Allocation get_in() {
        return mExportVar_in;
    }

    private final static int mExportVarIdx_script = 2;
    private Script mExportVar_script;
    public synchronized void set_script(Script v) {
        setVar(mExportVarIdx_script, v);
        mExportVar_script = v;
    }

    public Script get_script() {
        return mExportVar_script;
    }

    private final static int mExportVarIdx_width = 3;
    private int mExportVar_width;
    public synchronized void set_width(int v) {
        setVar(mExportVarIdx_width, v);
        mExportVar_width = v;
    }

    public int get_width() {
        return mExportVar_width;
    }

    private final static int mExportVarIdx_height = 4;
    private int mExportVar_height;
    public synchronized void set_height(int v) {
        setVar(mExportVarIdx_height, v);
        mExportVar_height = v;
    }

    public int get_height() {
        return mExportVar_height;
    }

    private final static int mExportForEachIdx_root = 0;
    public void forEach_root(Allocation ain, Allocation aout) {
        // check ain
        if (!ain.getType().getElement().isCompatible(__U8_4)) {
            throw new RSRuntimeException("Type mismatch with U8_4!");
        }
        // check aout
        if (!aout.getType().getElement().isCompatible(__U8_4)) {
            throw new RSRuntimeException("Type mismatch with U8_4!");
        }
        // Verify dimensions
        Type tIn = ain.getType();
        Type tOut = aout.getType();
        if ((tIn.getCount() != tOut.getCount()) ||
            (tIn.getX() != tOut.getX()) ||
            (tIn.getY() != tOut.getY()) ||
            (tIn.getZ() != tOut.getZ()) ||
            (tIn.hasFaces() != tOut.hasFaces()) ||
            (tIn.hasMipmaps() != tOut.hasMipmaps())) {
            throw new RSRuntimeException("Dimension mismatch between input and output parameters!");
        }
        forEach(mExportForEachIdx_root, ain, aout, null);
    }

    private final static int mExportFuncIdx_filter = 0;
    public void invoke_filter() {
        invoke(mExportFuncIdx_filter);
    }

}

