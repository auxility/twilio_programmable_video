package twilio.flutter.twilio_programmable_video

import android.view.View
import com.twilio.video.VideoTrack
import com.twilio.video.VideoView
import io.flutter.plugin.platform.PlatformView

class ParticipantView(private var videoView: VideoView, videoTrack: VideoTrack) : PlatformView {
    private val TAG = "ParticipantView"

    init {
        videoTrack.addSink(videoView)
    }

    override fun getView(): View {
        return videoView
    }

    override fun dispose() {
        debug("dispose => Disposing ParticipantView")
    }

    internal fun debug(msg: String) {
        TwilioProgrammableVideoPlugin.debug("$TAG::$msg")
    }
}
