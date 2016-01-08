/*
 * Copyright 2012 Google Inc. All Rights Reserved.
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

package com.adamhurwitz.android.popularmovies;

import android.os.Bundle;
import android.util.Log;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

/**
 * A simple YouTube Android API demo application which shows how to create a simple application that
 * shows a YouTube Video in a {@link YouTubePlayerFragment}.
 * <p>
 * Note, this sample app extends from {@link YouTubeFailureRecoveryActivity} to handle errors, which
 * itself extends {@link YouTubeBaseActivity}. However, you are not required to extend
 * {@link YouTubeBaseActivity} if using {@link YouTubePlayerFragment}s.
 */
public class FragmentDemoActivity extends YouTubeFailureRecoveryActivity {
    private final String LOG_TAG = FragmentDemoActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_detail);

        YouTubePlayerFragment youTubePlayerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubePlayerFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);
        Log.v(LOG_TAG, "Get ID for YouTube Player Fragment");
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo("sGbxmsDFVnE");
        }
        Log.v(LOG_TAG, "Cue Video");
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        Log.v(LOG_TAG, "Return YouTube Fragment");
        return (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
    }

}
