package com.jerael.mesagisto.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.widget.SeekBar
import com.jerael.mesagisto.database.getFileFromStorage
import java.io.File

class AppVoicePlayer {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var file: File
    private lateinit var seekBar: SeekBar
    private lateinit var runnable: Runnable
    private val handler = android.os.Handler()

    fun play(function: () -> Unit) {

        if (seekBar.progress != 0) {
            mediaPlayer.seekTo(seekBar.progress)
        } else {
            seekBar.max = mediaPlayer.duration
        }

        mediaPlayer.start()
        handler.postDelayed(runnable, 0)

        seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        mediaPlayer.setOnCompletionListener {
            pause() {
                mediaPlayer.stop()
                mediaPlayer.reset()
                seekBar.progress = 0
                function()
            }
        }
    }

    fun create(context: Context, fileUrl: String, messageKey: String, newSeekBar: SeekBar, function: () -> Unit) {

        seekBar = newSeekBar

        file = File(context.filesDir, messageKey)

        if (file.exists() && file.length() > 0 && file.isFile) {
            mediaPlayer = MediaPlayer.create(context, Uri.fromFile(file))

            runnable = Runnable {
                seekBar.progress = mediaPlayer.currentPosition
                handler.postDelayed(runnable, 500)
            }

            function()

        } else {
            file.createNewFile()
            getFileFromStorage(file, fileUrl) {

                mediaPlayer = MediaPlayer.create(context, Uri.fromFile(file))

                runnable = Runnable {
                    seekBar.progress = mediaPlayer.currentPosition
                    handler.postDelayed(runnable, 500)
                }

                function()
            }
        }

    }

    fun pause(function: () -> Unit) {
        mediaPlayer.pause()
        handler.removeCallbacks(runnable)
        function()
    }

    fun init() {
        mediaPlayer = MediaPlayer()
    }
}