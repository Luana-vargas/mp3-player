package dominando.android.mp3service

import android.os.Binder

interface Mp3Service {
    fun play(file: String)
    fun pause()
    fun stop()
    val currentSong: String?
    val totalTime: Int
    val elapsedTime: Int
}


