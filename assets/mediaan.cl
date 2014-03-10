float mergesort(float data[],int lengte){
  int i1=0, i2=0;         // huidige plaats in groepen
  float *groep1, *groep2;   // begin van groepen
  int lengte1, lengte2;   // lengtes van groepen
  float gesorteerd[lengte]; // gesorteerde data
  int tijdelijk;
 
  if (lengte > 1){
    // indien lengte 1 of kleiner is valt er niets te sorteren
    // verdeel in groepen
    groep1= data;
    groep2= data + lengte / 2;
    // zoek lengte van elke groep
    lengte1= lengte / 2;
    lengte2= lengte - lengte1;
    // mergesort
    mergesort(groep1, lengte1);
    mergesort(groep2, lengte2);
 
    // merge
    for (tijdelijk= 0; tijdelijk < lengte; tijdelijk++){
      if (i1==lengte1){
        // einde groep1, neem huidig van 2
        gesorteerd[tijdelijk]= groep2[i2];
        i2++;
      } else if (i2==lengte2){
        // einde groep2,neem huidig van 1
        gesorteerd[tijdelijk]= groep1[i1];
        i1++;
      } else if (groep1[i1] < groep2[i2]){
        // huidig van 1 is kleiner,neem dit
        gesorteerd[tijdelijk]= groep1[i1];
        i1++;
      } else{
        // huidig van 2 is kleiner,neem dit
        gesorteerd[tijdelijk]= groep2[i2];
        i2++;
      }
    }
    // kopieer gesorteerd naar data
    //memcpy(gesorteerd, data, lengte* sizeof(float));
    return gesorteerd[4];
  }
}

kernel void mediaanKernel
(
    global const uchar4* inputPixels,
    global uchar4* outputPixels,
    const uint rowPitch,
    const uint width,
    const uint height
)
{
    int x = get_global_id(0);
    int y = get_global_id(1);
    int centerIndex = y * width + x;
    float4 sum4 = (float4)0.0f;
    float4 neighbours[9];
	if(get_global_id(0) < 1 || get_global_id(0) > width - 2 || get_global_id(1) < 1 || get_global_id(1) > height - 2)
	{
		return;
	}
	float4 centerPixel;
	neighbours[0] = convert_float4(inputPixels[(y-1)*width +x -1]);
	neighbours[1] = convert_float4(inputPixels[(y-1)*width +x]);
	neighbours[2] = convert_float4(inputPixels[(y-1)*width +x +1]);

	neighbours[3] = convert_float4(inputPixels[(y)*width +x -1]);
	neighbours[4] = convert_float4(inputPixels[(y)*width +x]);
	neighbours[5] = convert_float4(inputPixels[(y)*width +x +1]);
	
	neighbours[6] = convert_float4(inputPixels[(y+1)*width +x -1]);
	neighbours[7] = convert_float4(inputPixels[(y+1)*width +x]);
	neighbours[8] = convert_float4(inputPixels[(y+1)*width +x +1]);
		
	float r0[9];
	for(int i=0;i<3;i++)
	{
		if(i==0){
			r0[0]=	neighbours[0].x;
			r0[1]=	neighbours[1].x; 
			r0[2]=	neighbours[2].x;
			r0[3]=	neighbours[3].x;
			r0[4]=	neighbours[4].x;
			r0[5]=	neighbours[5].x;
			r0[6]=	neighbours[6].x;
			r0[7]=	neighbours[7].x;
			r0[8]=	neighbours[8].x;	
		}
		else if(i==1){
			//waarde in output array toevoegen
			//centerPixel.x = r0[4];
			centerPixel.x = mergesort(r0,9);
			
			r0[0]=	neighbours[0].y;
			r0[1]=	neighbours[1].y; 
			r0[2]=	neighbours[2].y;
			r0[3]=	neighbours[3].y;
			r0[4]=	neighbours[4].y;
			r0[5]=	neighbours[5].y;
			r0[6]=	neighbours[6].y;
			r0[7]=	neighbours[7].y;
			r0[8]=	neighbours[8].y;		
		}
		else if(i==2){
			//waarde in output array toevoegen
			//centerPixel.y = r0[4];
			centerPixel.y = mergesort(r0,9);
			
			r0[0]=	neighbours[0].z;
			r0[1]=	neighbours[1].z; 
			r0[2]=	neighbours[2].z;
			r0[3]=	neighbours[3].z;
			r0[4]=	neighbours[4].z;
			r0[5]=	neighbours[5].z;
			r0[6]=	neighbours[6].z;
			r0[7]=	neighbours[7].z;
			r0[8]=	neighbours[8].z;			
		}	
        //mergesort(r0,9);
	}
	//centerPixel.z = r0[4];
	centerPixel.z = mergesort(r0,9);
    outputPixels[centerIndex] = convert_uchar4_sat_rte(centerPixel);
}