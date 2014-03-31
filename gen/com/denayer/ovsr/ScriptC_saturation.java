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
 * The source Renderscript file: /home/koen/git/OVSR/src/com/denayer/ovsr/saturation.rs
 */
package com.denayer.ovsr;

import android.renderscript.*;
import android.content.res.Resources;

/**
 * @hide
 */
public class ScriptC_saturation extends ScriptC {
    private static final String __rs_resource_name = "saturation";
    // Constructor
    public  ScriptC_saturation(RenderScript rs) {
        this(rs,
             rs.getApplicationContext().getResources(),
             rs.getApplicationContext().getResources().getIdentifier(
                 __rs_resource_name, "raw",
                 rs.getApplicationContext().getPackageName()));
    }

    public  ScriptC_saturation(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        __U8_4 = Element.U8_4(rs);
    }

    private Element __U8_4;
    private FieldPacker __rs_fp_ALLOCATION;
    private FieldPacker __rs_fp_F32;
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

    private final static int mExportVarIdx_timeAlloc = 2;
    private Allocation mExportVar_timeAlloc;
    public synchronized void set_timeAlloc(Allocation v) {
        setVar(mExportVarIdx_timeAlloc, v);
        mExportVar_timeAlloc = v;
    }

    public Allocation get_timeAlloc() {
        return mExportVar_timeAlloc;
    }

    private final static int mExportVarIdx_script = 3;
    private Script mExportVar_script;
    public synchronized void set_script(Script v) {
        setVar(mExportVarIdx_script, v);
        mExportVar_script = v;
    }

    public Script get_script() {
        return mExportVar_script;
    }

    private final static int mExportVarIdx_saturation = 4;
    private float mExportVar_saturation;
    public synchronized void set_saturation(float v) {
        setVar(mExportVarIdx_saturation, v);
        mExportVar_saturation = v;
    }

    public float get_saturation() {
        return mExportVar_saturation;
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

