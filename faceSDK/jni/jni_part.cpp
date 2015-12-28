#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>
#include <android/log.h>
//new
#include <stdio.h>
#include <math.h>
#include "opencv2\opencv.hpp"
#include "asmmodel.h"
#include "modelfile.h"
#include <cstdio>
#include <string>
//new
using namespace std;
using namespace cv;

//new
using namespace StatModel;

using std::string;
using cv::imshow;
using std::cerr;
using std::endl;


//new

ASMModel asmModel;
cv::CascadeClassifier faceCascade;
vector< Point_<int> > V;
bool isFound = false;
char *TAG = "faceSDK:Native";

extern "C" {
JNIEXPORT bool JNICALL Java_com_jeikei_facelibrary_fstLibrary_FindFeatures(JNIEnv* env, jobject thiz, jint width, jint height, jbyteArray yuv, jintArray bgra)
{
	isFound = false;

	clock_t startTime, endTime;
	char ch_debug[512];

	startTime = clock();

    jbyte* _yuv  = env->GetByteArrayElements(yuv, 0);
    jint*  _bgra = env->GetIntArrayElements(bgra, 0);
//__android_log_write(ANDROID_LOG_DEBUG,"Tag","tukareta00");
    Mat myuv(height + height/2, width, CV_8UC1, (unsigned char *)_yuv);
    Mat mbgra(height, width, CV_8UC4, (unsigned char *)_bgra);
    Mat mgray(height, width, CV_8UC1, (unsigned char *)_yuv);
//__android_log_write(ANDROID_LOG_DEBUG,"Tag","tukareta01");
    //Please make attention about BGRA byte order
    //ARGB stored in java as int array becomes BGRA at native level
    cvtColor(myuv, mbgra, CV_YUV420sp2BGR, 4);

	///////////START image rotation
	/*
	CvPoint2D32f rot_center = cvPoint2D32f(width/2 , height/2);
	CvMat *rot_mat = cvCreateMat( 2, 3, CV_32FC1);
	cv2DRotationMatrix( rot_center, // Source Image의 센터를 정한다.
						90,  // 이것은 각도 + 값은 시계 반대 반대 방향을 의미한다.
						1,  // 이미지 크기(scale)... 
						rot_mat); // 결과를 저장하는 매트릭스 이다.

	cvWarpAffine(&mbgra, &mbgra, 
                rot_mat, 
                CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS,
                cvScalarAll(0)); // 선형보간
	__android_log_write(ANDROID_LOG_DEBUG,"Tag","tukareta03");
	*/
	///////////END image rotation
//__android_log_write(ANDROID_LOG_DEBUG,"Tag","tukareta03");
    //vector<KeyPoint> v;
	endTime = clock();
	sprintf(ch_debug, "time1 = %d", endTime-startTime);
	__android_log_write(ANDROID_LOG_DEBUG,TAG, ch_debug );
    //FastFeatureDetector detector(50);
    //detector.detect(mgray, v);
    //for( size_t i = 0; i < v.size(); i++ )
        //circle(mbgra, Point(v[i].pt.x, v[i].pt.y), 10, Scalar(0,0,255,255));

    //Mat img;
    //cv::flip(mbgra, img, 1);
//__android_log_write(ANDROID_LOG_DEBUG,"Tag","tukareta04");
	startTime = clock();
    vector< cv::Rect > faces;
    faceCascade.detectMultiScale( mbgra, faces, 1.2, 2, CV_HAAR_FIND_BIGGEST_OBJECT|CV_HAAR_SCALE_IMAGE, Size(160, 160) );
//__android_log_write(ANDROID_LOG_DEBUG,"Tag","tukareta05");
    vector < ASMFitResult > fitResult = asmModel.fitAll(mbgra, faces, 0);
//__android_log_write(ANDROID_LOG_DEBUG,"Tag","tukareta06");
    //asmModel.showResult(mbgra, fitResult);
//__android_log_write(ANDROID_LOG_DEBUG,"Tag","tukareta07");
////__android_log_write(ANDROID_LOG_DEBUG,"Tag","new draw00");
    for (size_t i=0; i<fitResult.size(); i++){
////__android_log_write(ANDROID_LOG_DEBUG,"Tag","new draw00_0");
        fitResult[i].toPointList(V);
		//sprintf(ch_debug, "result size : %d", fitResult.size() );
		//__android_log_write(ANDROID_LOG_DEBUG,"Tag", ch_debug);
		isFound = true;
/*
		for(int k=0; k<30; k++){
          circle(mbgra, Point(V[k*2].x, V[k*2].y), 3, Scalar(0,0,255,255));
        }

*/
	endTime = clock();
	sprintf(ch_debug, "time2 = %d", endTime-startTime);
	__android_log_write(ANDROID_LOG_DEBUG, TAG, ch_debug );

		for(int k=48; k<66; k++){
			//char s_test[512];
			//sprintf(s_test, "X(%d) = %d, Y(%d) = %d\n", k+1, V[k].x, k+1, V[k].y);

          circle(mbgra, Point(V[k].x, V[k].y), 3, Scalar(0,0,255,255));


			//__android_log_write(ANDROID_LOG_DEBUG,"Tag", s_test );
        }
		 circle(mbgra, Point(V[51].x, V[51].y), 3, Scalar(0,255,0,255));
		 circle(mbgra, Point(V[57].x, V[57].y), 3, Scalar(0,255,0,255));

		 circle(mbgra, Point(V[48].x, V[48].y), 3, Scalar(255,0,0,255));
		 circle(mbgra, Point(V[54].x, V[54].y), 3, Scalar(255,0,0,255));

//	__android_log_write(ANDROID_LOG_DEBUG,"Tag","new draw00_2");
    }
  
////__android_log_write(ANDROID_LOG_DEBUG,"Tag","new draw01");
    env->ReleaseIntArrayElements(bgra, _bgra, 0);
    env->ReleaseByteArrayElements(yuv, _yuv, 0);

	return isFound;
}

char* jstring2String(JNIEnv* env, jstring jstr) 
{ 
    char* rtn = NULL; 
    jclass clsstring = env->FindClass("java/lang/String"); 
    jstring strencode = env->NewStringUTF("utf-8"); 
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B"); 
    jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode); 
    jsize alen = env->GetArrayLength(barr); 
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE); 
    if (alen > 0) 
    { 
        rtn = (char*)malloc(alen + 1); 
        memcpy(rtn, ba, alen); 
        rtn[alen] = 0; 
    } 
    env->ReleaseByteArrayElements(barr, ba, 0); 
    return rtn; 
}

JNIEXPORT void JNICALL Java_com_jeikei_facelibrary_fstLibrary_readASMModel(JNIEnv* env, jobject thiz, jstring xml, jstring faceCascadePath)
{
    //const char * c_xml = (char *)env->GetStringUTFChars(xml, JNI_FALSE);
	//__android_log_write(ANDROID_LOG_DEBUG,"Tag",jstring2String(env,xml));
    asmModel.loadFromFile(jstring2String(env,xml));
    //__android_log_write(ANDROID_LOG_DEBUG,"Tag","01yomikomi");
    //delete c_xml;
    //env->ReleaseStringCritical(xml, c_xml);
	//__android_log_write(ANDROID_LOG_DEBUG,"Tag",jstring2String(env,faceCascadePath));
    faceCascade.load(jstring2String(env,faceCascadePath));
    //__android_log_write(ANDROID_LOG_DEBUG,"Tag","02yomikomi");
}

JNIEXPORT int JNICALL Java_com_jeikei_facelibrary_fstLibrary_getShape(JNIEnv* env, jobject thiz, jint xy, jint idx)
{
	if(isFound == false) return -1;

	if(idx < 0 || idx > 75)	idx = 0;

	switch(xy)
	{
	case 0:
		return V[idx].x;
	case 1:
		return V[idx].y;
	default:
		return V[idx].x;
	}
}
}
