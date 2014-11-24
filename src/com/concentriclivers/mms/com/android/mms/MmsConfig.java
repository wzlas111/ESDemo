/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.concentriclivers.mms.com.android.mms;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

//import com.android.internal.telephony.TelephonyProperties;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

public class MmsConfig {
    private static final String TAG = "MmsConfig";
    private static final boolean DEBUG = true;
    private static final boolean LOCAL_LOGV = false;

    private static final String DEFAULT_HTTP_KEY_X_WAP_PROFILE = "x-wap-profile";
    private static final String DEFAULT_USER_AGENT = "Android-Mms/2.0";

    private static final int MAX_IMAGE_HEIGHT = 480;
    private static final int MAX_IMAGE_WIDTH = 640;
    private static final int MAX_TEXT_LENGTH = 2000;

    /**
     * Whether to hide MMS functionality from the user (i.e. SMS only).
     */
    private static boolean mTransIdEnabled = false;
    private static int mMmsEnabled = 1;                         // default to true
    private static int mMaxMessageSize = 300 * 1024;            // default to 300k max size
    private static String mUserAgent = DEFAULT_USER_AGENT;
    private static String mUaProfTagName = DEFAULT_HTTP_KEY_X_WAP_PROFILE;
    private static String mUaProfUrl = null;
    private static String mHttpParams = null;
    private static String mHttpParamsLine1Key = null;
    private static String mEmailGateway = null;
    private static int mMaxImageHeight = MAX_IMAGE_HEIGHT;      // default value
    private static int mMaxImageWidth = MAX_IMAGE_WIDTH;        // default value
    private static int mRecipientLimit = Integer.MAX_VALUE;     // default value
    private static int mDefaultSMSMessagesPerThread = 500;      // default value
    private static int mDefaultMMSMessagesPerThread = 50;       // default value
    private static int mMinMessageCountPerThread = 2;           // default value
    private static int mMaxMessageCountPerThread = 5000;        // default value
    private static int mHttpSocketTimeout = 60*1000;            // default to 1 min
    private static int mMinimumSlideElementDuration = 7;        // default to 7 sec
    private static boolean mNotifyWapMMSC = false;
    private static boolean mAllowAttachAudio = true;

    // If mEnableMultipartSMS is true, long sms messages are always sent as multi-part sms
    // messages, with no checked limit on the number of segments.
    // If mEnableMultipartSMS is false, then as soon as the user types a message longer
    // than a single segment (i.e. 140 chars), then the message will turn into and be sent
    // as an mms message. This feature exists for carriers that don't support multi-part sms's.
    private static boolean mEnableMultipartSMS = true;

    private static boolean mEnableSlideDuration = true;
    private static boolean mEnableMMSReadReports = true;        // key: "enableMMSReadReports"
    private static boolean mEnableSMSDeliveryReports = true;    // key: "enableSMSDeliveryReports"
    private static boolean mEnableMMSDeliveryReports = true;    // key: "enableMMSDeliveryReports"
    private static int mMaxTextLength = -1;

    // This is the max amount of storage multiplied by mMaxMessageSize that we
    // allow of unsent messages before blocking the user from sending any more
    // MMS's.
    private static int mMaxSizeScaleForPendingMmsAllowed = 4;       // default value

    // Email gateway alias support, including the master switch and different rules
    private static boolean mAliasEnabled = false;
    private static int mAliasRuleMinChars = 2;
    private static int mAliasRuleMaxChars = 48;

    private static int mMaxSubjectLength = 40;  // maximum number of characters allowed for mms
                                                // subject

    public static void init(Context context) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "MmsConfig.init()");
        }
        // Always put the mnc/mcc in the log so we can tell which mms_config.xml was loaded.
		// TDH: Comment out log.
//        Log.v(TAG, "mnc/mcc: " +
//                android.os.SystemProperties.get(TelephonyProperties.PROPERTY_ICC_OPERATOR_NUMERIC));
    }

    public static boolean getMmsEnabled() {
        return mMmsEnabled == 1 ? true : false;
    }

    public static int getMaxMessageSize() {
        if (LOCAL_LOGV) {
            Log.v(TAG, "MmsConfig.getMaxMessageSize(): " + mMaxMessageSize);
        }
       return mMaxMessageSize;
    }

    /**
     * This function returns the value of "enabledTransID" present in mms_config file.
     * In case of single segment wap push message, this "enabledTransID" indicates whether
     * TransactionID should be appended to URI or not.
     */
    public static boolean getTransIdEnabled() {
        return mTransIdEnabled;
    }

    public static String getUserAgent() {
        return mUserAgent;
    }

    public static String getUaProfTagName() {
        return mUaProfTagName;
    }

    public static String getUaProfUrl() {
        return mUaProfUrl;
    }

    public static String getHttpParams() {
        return mHttpParams;
    }

    public static String getHttpParamsLine1Key() {
        return mHttpParamsLine1Key;
    }

    public static String getEmailGateway() {
        return mEmailGateway;
    }

    public static int getMaxImageHeight() {
        return mMaxImageHeight;
    }

    public static int getMaxImageWidth() {
        return mMaxImageWidth;
    }

    public static int getRecipientLimit() {
        return mRecipientLimit;
    }

    public static int getMaxTextLimit() {
        return mMaxTextLength > -1 ? mMaxTextLength : MAX_TEXT_LENGTH;
    }

    public static int getDefaultSMSMessagesPerThread() {
        return mDefaultSMSMessagesPerThread;
    }

    public static int getDefaultMMSMessagesPerThread() {
        return mDefaultMMSMessagesPerThread;
    }

    public static int getMinMessageCountPerThread() {
        return mMinMessageCountPerThread;
    }

    public static int getMaxMessageCountPerThread() {
        return mMaxMessageCountPerThread;
    }

    public static int getHttpSocketTimeout() {
        return mHttpSocketTimeout;
    }

    public static int getMinimumSlideElementDuration() {
        return mMinimumSlideElementDuration;
    }

    public static boolean getMultipartSmsEnabled() {
        return mEnableMultipartSMS;
    }

    public static boolean getSlideDurationEnabled() {
        return mEnableSlideDuration;
    }

    public static boolean getMMSReadReportsEnabled() {
        return mEnableMMSReadReports;
    }

    public static boolean getSMSDeliveryReportsEnabled() {
        return mEnableSMSDeliveryReports;
    }

    public static boolean getMMSDeliveryReportsEnabled() {
        return mEnableMMSDeliveryReports;
    }

    public static boolean getNotifyWapMMSC() {
        return mNotifyWapMMSC;
    }

    public static int getMaxSizeScaleForPendingMmsAllowed() {
        return mMaxSizeScaleForPendingMmsAllowed;
    }

    public static boolean isAliasEnabled() {
        return mAliasEnabled;
    }

    public static int getAliasMinChars() {
        return mAliasRuleMinChars;
    }

    public static int getAliasMaxChars() {
        return mAliasRuleMaxChars;
    }

    public static boolean getAllowAttachAudio() {
        return mAllowAttachAudio;
    }

    public static int getMaxSubjectLength() {
        return mMaxSubjectLength;
    }

    public static final void beginDocument(XmlPullParser parser, String firstElementName) throws XmlPullParserException, IOException
    {
        int type;
        while ((type=parser.next()) != parser.START_TAG
                   && type != parser.END_DOCUMENT) {
            ;
        }

        if (type != parser.START_TAG) {
            throw new XmlPullParserException("No start tag found");
        }

        if (!parser.getName().equals(firstElementName)) {
            throw new XmlPullParserException("Unexpected start tag: found " + parser.getName() +
                    ", expected " + firstElementName);
        }
    }

    public static final void nextElement(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        int type;
        while ((type=parser.next()) != parser.START_TAG
                   && type != parser.END_DOCUMENT) {
            ;
        }
    }


}
