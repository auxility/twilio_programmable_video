package twilio.flutter.twilio_programmable_video

import android.content.Context
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.twilio.video.VideoScaleType
import com.twilio.video.VideoTrack
import com.twilio.video.VideoView
import io.flutter.plugin.common.MessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class ParticipantViewFactory(
    createArgsCodec: MessageCodec<Any>,
    private val plugin: PluginHandler
) : PlatformViewFactory(createArgsCodec) {
    private val TAG = "RoomListener"

    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        var videoTrack: VideoTrack? = null
        val params = args as? Map<*, *> ?: throw IllegalStateException("args cannot be null")

        if (params["isLocal"] == true) {
            debug("create => constructing local view with params: '${params.values.joinToString(", ")}'")
            val localVideoTrackName = params["name"] as? String ?: ""
            if (localVideoTrackName != "" && localVideoTrackName in TwilioProgrammableVideoPlugin.localVideoTracks) {
                videoTrack = TwilioProgrammableVideoPlugin.localVideoTracks[localVideoTrackName]
            } else {
                val localParticipant = plugin.getLocalParticipant()
                if (localParticipant?.localVideoTracks?.isNotEmpty() == true) {
                    videoTrack = localParticipant.localVideoTracks.firstOrNull()?.localVideoTrack
                }
            }
        } else {
            debug("create => constructing view with params: '${params.values.joinToString(", ")}'")
            if ("remoteParticipantSid" in params && "remoteVideoTrackSid" in params) {
                val remoteParticipant =
                    plugin.getRemoteParticipant(params["remoteParticipantSid"] as String)
                val remoteVideoTrack =
                    remoteParticipant?.remoteVideoTracks?.find { it.trackSid == params["remoteVideoTrackSid"] }
                if (remoteParticipant != null && remoteVideoTrack != null) {
                    videoTrack = remoteVideoTrack.remoteVideoTrack
                }
            }
        }

        if (videoTrack == null) {
            throw IllegalStateException("Could not create VideoTrack")
        }
        val videoView = VideoView(context as Context)
        videoView.mirror = params["mirror"] as Boolean

        // fit video
        videoView.videoScaleType = VideoScaleType.ASPECT_FIT
        // VideoScaleType requires WRAP_CONTENT to work properly
        // related issue https://github.com/twilio/video-quickstart-android/issues/381
        videoView.layoutParams = FrameLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT,
        )

        return ParticipantView(videoView, videoTrack)
    }

    internal fun debug(msg: String) {
        TwilioProgrammableVideoPlugin.debug("$TAG::$msg")
    }
}
