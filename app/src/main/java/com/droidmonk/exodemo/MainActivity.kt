package com.droidmonk.exodemo

import android.net.Uri
import android.os.Bundle
import android.view.MenuInflater
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ads.AdsMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*

class MainActivity : AppCompatActivity() {
    private var currentPosition: Long = 0
    private lateinit var adsLoader: ImaAdsLoader
    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adsLoader = ImaAdsLoader(this, Uri.parse(resources.getString(R.string.ad_tag)))

        btn_speed.setOnClickListener {
            val popup = PopupMenu(this, it)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.playback_speed, popup.menu)
            popup.show()

            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.half_x -> {
                        player?.setPlaybackParameters(PlaybackParameters(0.5f))
                        true
                    }
                    R.id.one_x -> {
                        player?.setPlaybackParameters(PlaybackParameters(1f))
                        true
                    }
                    R.id.two_x -> {
                        player?.setPlaybackParameters(PlaybackParameters(2f))
                        true
                    }
                    R.id.three_x -> {
                        player?.setPlaybackParameters(PlaybackParameters(3f))
                        true
                    }
                    else -> {
                        Toast.makeText(this, "Invalid Option", Toast.LENGTH_LONG).show()
                        true
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        player_view.player = player
        adsLoader.setPlayer(player)

        val dataSourceFactory = DefaultDataSourceFactory(
            this,
            "ExoDemo"
        )

//        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
//            .createMediaSource(Uri.parse(resources.getString(R.string.media_url_mp4)))
        val mediaSource: MediaSource = DashMediaSource.Factory(dataSourceFactory).createMediaSource(
            Uri.parse(
                resources.getString(R.string.media_url_dash)
            )
        )
        val adsMediaSource = AdsMediaSource(mediaSource, dataSourceFactory, adsLoader, player_view)

        player?.prepare(adsMediaSource)
        player?.seekTo(currentPosition)
        player?.setPlayWhenReady(true)
    }

    override fun onStop() {
        super.onStop()
        adsLoader.setPlayer(null)
        currentPosition = player!!.currentPosition
        player_view.player = null
        player!!.release()
        player = null
    }
}
