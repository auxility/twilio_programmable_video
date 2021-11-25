package twilio.flutter.twilio_programmable_video

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.twilio.video.VideoScaleType
import com.twilio.video.VideoTrack
import com.twilio.video.VideoView
import io.flutter.plugin.common.MessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class ParticipantViewFactory(createArgsCodec: MessageCodec<Any>, private val plugin: PluginHandler) : PlatformViewFactory(createArgsCodec) {
    private val TAG = "RoomListener"

    override fun create(context: Context, viewId: Int, args: Any?): PlatformView? {
        var videoTrack: VideoTrack? = null

        if (args != null) {
            val params = args as Map<String, Any>
            if (params.containsKey("isLocal")) {
                debug("create => constructing local view")
                val localParticipant = plugin.getLocalParticipant()
                if (localParticipant != null && localParticipant.localVideoTracks != null && localParticipant.localVideoTracks?.size != 0) {
                    videoTrack = localParticipant.localVideoTracks!![0].localVideoTrack
                }
            } else {
                debug("create => constructing view with params: '${params.values.joinToString(", ")}'")
                if (params.containsKey("remoteParticipantSid") && params.containsKey("remoteVideoTrackSid")) {
                    val remoteParticipant = plugin.getRemoteParticipant(params["remoteParticipantSid"] as String)
                    val remoteVideoTrack = remoteParticipant?.remoteVideoTracks?.find { it.trackSid == params["remoteVideoTrackSid"] }
                    if (remoteParticipant != null && remoteVideoTrack != null) {
                        videoTrack = remoteVideoTrack.remoteVideoTrack
                    }
                }
            }

            if (videoTrack != null) {
                val videoView = VideoView(context)
                videoView.mirror = params["mirror"] as Boolean

                // fit video
                videoView.videoScaleType = VideoScaleType.ASPECT_FIT

                // VideoScaleType requires WRAP_CONTENT to work properly
                // related issue https://github.com/twilio/video-quickstart-android/issues/381
                videoView.layoutParams = FrameLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                )

                return ParticipantView(videoView, videoTrack)
            }
        }

        return null
    }

    internal fun debug(msg: String) {
        TwilioProgrammableVideoPlugin.debug("$TAG::$msg")
    }
}
