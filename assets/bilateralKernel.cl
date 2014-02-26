//
//  clKernelsExample.cl
//  OpenCL Example1
//
//  Created by Rasmusson, Jim on 18/03/13.
//
//  Copyright (c) 2013, Sony Mobile Communications AB
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions are met:
//
//     * Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
//
//     * Redistributions in binary form must reproduce the above copyright
//       notice, this list of conditions and the following disclaimer in the
//       documentation and/or other materials provided with the distribution.
//
//     * Neither the name of Sony Mobile Communications AB nor the
//       names of its contributors may be used to endorse or promote products
//       derived from this software without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//  DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
//  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
//  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

#define sigmaDomain 3.0f
#define sigmaRange  0.2f
#define oneover255 0.00392156862745f
#define filterWidth 2

__kernel void bilateralFilterKernel(__global uchar4 *srcBuffer,
                                    __global uchar4 *dstBuffer, 
                                    const int width, const int height)
{
    int x = get_global_id(0);
    int y = get_global_id(1);
    int centerIndex = y * width + x;
    float4  sum4 = (float4)0.0f;
        
	if ( (x >= filterWidth) && (x < (width - filterWidth)) &&     //avoid reading outside of buffer
         (y >= filterWidth) && (y < (height - filterWidth)) )
	{
		float4 centerPixel = oneover255 * convert_float4(srcBuffer[centerIndex]);  
		float normalizeCoeff = 0.0f;
	    
		for (int yy=-filterWidth; yy<=filterWidth; yy++)
		{
			for (int xx=-filterWidth; xx<=filterWidth; xx++)
			{
				int thisIndex = (y+yy) * width + (x+xx); 
				float4 currentPixel = oneover255 * convert_float4(srcBuffer[thisIndex]);
                float domainDistance = fast_distance((float)(xx), (float)(yy));
 				float domainWeight = exp(-0.5f * pow((domainDistance/sigmaDomain),2.0f));
                
				float rangeDistance = fast_distance(currentPixel.xyz, centerPixel.xyz);
				float rangeWeight = exp(-0.5f * pow((rangeDistance/sigmaRange),2.0f));
	            
	            float totalWeight = domainWeight * rangeWeight;
	            normalizeCoeff += totalWeight;
				sum4 += totalWeight * currentPixel;
			}
		}
		sum4 /= normalizeCoeff;
        sum4.w = 1.0f; // set alpha to fully opaque
	}
	dstBuffer[centerIndex] = convert_uchar4_sat_rte(255.0f * sum4);
}

