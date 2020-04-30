package dominando.android.mp3service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.PlaybackState.*
import android.os.IBinder
import java.io.FileInputStream

//A classe herda de Service e implementa a interface Mp3Service
class Mp3ServiceImpl : Service(), Mp3Service {
    private lateinit var mediaPlayer: MediaPlayer
    private var isPaused: Boolean = false
    private var currentFile: String? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }
    //Quando uma activity conecta-se a um serviço invocando o metodo bindService(Intent), o
    // onBind(Intent) é chamado, e nele é retornada uma instância do Mp3Binder.
    //para criar o binder é preciso uma instância de mp3Service; como o Mp3ServiceImpl implementa essa
    //interface, é passado this.
    override fun onBind(intent: Intent): IBinder? {
        return Mp3Binder(this)
    }

    //o metodo onStartCommand é disparado smp que é feita uma chamada ao metodo startService(Intent)
    //nele checamos o parametro extra_action, q indica qual operação com o audio deve ser realizada
    // (tocar, pausar ou parar). De acordo com a ação, é chamado o metodo correspondente: play (string),
    // pause() ou stop ()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.getStringExtra(EXTRA_ACTION)) {
                ACTION_PLAY -> play(intent.getStringExtra(EXTRA_FILE))
                ACTION_PAUSE -> pause()
                ACTION_STOP -> stop()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // Implementação da interface Mp3Service
    override fun play(file: String) {
        if (!mediaPlayer.isPlaying && !isPaused) {
            try {
                mediaPlayer.reset()
                // abrindo um fileinputstream p o caminho do arquivo passado como parametro
                //com esse objeto, o file descriptor é obtido por meio da propriedade fd, que é
                // passado como parametro p o metodo setdatasource do mediaplayer
                val fis = FileInputStream(file)
                mediaPlayer.setDataSource(fis.fd)
                mediaPlayer.prepare()
                currentFile = file
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
        }
        isPaused = false
        mediaPlayer.start()
    }

    override fun pause() {
        if (mediaPlayer.isPlaying) {
            isPaused = true
            mediaPlayer.pause()
        }
    }

    override fun stop() {
        if (mediaPlayer.isPlaying || isPaused) {
            isPaused = false
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
    }

    override var currentSong: String? = null
        get() = currentFile

    override var totalTime: Int = 0
        get() {
            return if (mediaPlayer.isPlaying || isPaused) {
                mediaPlayer.duration
            } else 0
        }

    override var elapsedTime: Int = 0
        get() {
            return if (mediaPlayer.isPlaying || isPaused) {
                mediaPlayer.currentPosition
            } else 0
        }

    companion object {
        val EXTRA_ACTION = "${Mp3ServiceImpl::class.java.`package`?.name}.EXTRA_ACTION"
        val EXTRA_FILE = "${Mp3ServiceImpl::class.java.`package`?.name}.EXTRA_FILE"
        val ACTION_PLAY = "${Mp3ServiceImpl::class.java.`package`?.name}.ACTION_PLAY"
        val ACTION_PAUSE = "${Mp3ServiceImpl::class.java.`package`?.name}.ACTION_PAUSE"
        val ACTION_STOP = "${Mp3ServiceImpl::class.java.`package`?.name}.ACTION_STOP"
    }
}