// Copyright (c) 2009-2011 Intel Corporation
// All rights reserved.
//
// WARRANTY DISCLAIMER
//
// THESE MATERIALS ARE PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL INTEL OR ITS
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
// EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
// PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
// PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
// OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY OR TORT (INCLUDING
// NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THESE
// MATERIALS, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// Intel Corporation is the author of the Materials, and requests that all
// problem reports or change requests be submitted to it directly

// 3x3 median filter kernel based on partial sort

// Scalar version of kernel
__kernel void mediaanKernel(const __global uint* pSrc, __global uint* pDst, 
                                    const uint rowPitch,
                                    const int width2, 
                                    const int height2)
{
    const int x = get_global_id(0);
    const int y = get_global_id(1);

    const int width = get_global_size(0);
    const int iOffset = y * width;
    const int iPrev = iOffset - width;
    const int iNext = iOffset + width;

    uint uiRGBA[9];

    // get pixels within aperture
    uiRGBA[0] = pSrc[iPrev + x - 1];
    uiRGBA[1] = pSrc[iPrev + x];
    uiRGBA[2] = pSrc[iPrev + x + 1];

    uiRGBA[3] = pSrc[iOffset + x - 1];
    uiRGBA[4] = pSrc[iOffset + x];
    uiRGBA[5] = pSrc[iOffset + x + 1];

    uiRGBA[6] = pSrc[iNext + x - 1];
    uiRGBA[7] = pSrc[iNext + x];
    uiRGBA[8] = pSrc[iNext + x + 1];

    uint uiResult = 0;
    uint uiMask = 0xFF;

    for(int ch = 0; ch < 3; ch++)
    {

        // extract next color channel
        uint r0,r1,r2,r3,r4,r5,r6,r7,r8;
        r0=uiRGBA[0]& uiMask;
        r1=uiRGBA[1]& uiMask;
        r2=uiRGBA[2]& uiMask;
        r3=uiRGBA[3]& uiMask;
        r4=uiRGBA[4]& uiMask;
        r5=uiRGBA[5]& uiMask;
        r6=uiRGBA[6]& uiMask;
        r7=uiRGBA[7]& uiMask;
        r8=uiRGBA[8]& uiMask;

        // perform partial bitonic sort to find current channel median
        uint uiMin = min(r0, r1);
        uint uiMax = max(r0, r1);
        r0 = uiMin;
        r1 = uiMax;

        uiMin = min(r3, r2);
        uiMax = max(r3, r2);
        r3 = uiMin;
        r2 = uiMax;

        uiMin = min(r2, r0);
        uiMax = max(r2, r0);
        r2 = uiMin;
        r0 = uiMax;

        uiMin = min(r3, r1);
        uiMax = max(r3, r1);
        r3 = uiMin;
        r1 = uiMax;

        uiMin = min(r1, r0);
        uiMax = max(r1, r0);
        r1 = uiMin;
        r0 = uiMax;

        uiMin = min(r3, r2);
        uiMax = max(r3, r2);
        r3 = uiMin;
        r2 = uiMax;

        uiMin = min(r5, r4);
        uiMax = max(r5, r4);
        r5 = uiMin;
        r4 = uiMax;

        uiMin = min(r7, r8);
        uiMax = max(r7, r8);
        r7 = uiMin;
        r8 = uiMax;

        uiMin = min(r6, r8);
        uiMax = max(r6, r8);
        r6 = uiMin;
        r8 = uiMax;

        uiMin = min(r6, r7);
        uiMax = max(r6, r7);
        r6 = uiMin;
        r7 = uiMax;

        uiMin = min(r4, r8);
        uiMax = max(r4, r8);
        r4 = uiMin;
        r8 = uiMax;

        uiMin = min(r4, r6);
        uiMax = max(r4, r6);
        r4 = uiMin;
        r6 = uiMax;

        uiMin = min(r5, r7);
        uiMax = max(r5, r7);
        r5 = uiMin;
        r7 = uiMax;

        uiMin = min(r4, r5);
        uiMax = max(r4, r5);
        r4 = uiMin;
        r5 = uiMax;

        uiMin = min(r6, r7);
        uiMax = max(r6, r7);
        r6 = uiMin;
        r7 = uiMax;

        uiMin = min(r0, r8);
        uiMax = max(r0, r8);
        r0 = uiMin;
        r8 = uiMax;

        r4 = max(r0, r4);
        r5 = max(r1, r5);

        r6 = max(r2, r6);
        r7 = max(r3, r7);

        r4 = min(r4, r6);
        r5 = min(r5, r7);

        // store found median into result
        uiResult |= min(r4, r5);

        // update channel mask
        uiMask <<= 8;
    }

    // store result into memory
    pDst[iOffset + x] = uiResult;
}