package com.kurento.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kurento.kmf.content.ContentException;
import com.kurento.kmf.content.PlayRequest;
import com.kurento.kmf.content.PlayerHandler;
import com.kurento.kmf.content.PlayerService;
import com.kurento.kmf.content.internal.player.PlayRequestImpl;
import com.kurento.kmf.media.JackVaderFilter;
import com.kurento.kmf.media.MediaPipeline;
import com.kurento.kmf.media.MediaPipelineFactory;
import com.kurento.kmf.media.PlayerEndPoint;
import com.kurento.kms.api.MediaType;

@PlayerService(name="PlayerJonJackVaderHandler", path="/playerJsonJack", useControlProtocol=true)
public class PlayerJonJackVaderHandler implements PlayerHandler
{
	
	private static final Logger log = LoggerFactory
			.getLogger(PlayerHttpHandler.class);

	@Override
	public void onPlayRequest(PlayRequest playRequest) throws ContentException {
		log.info("Received request to " + playRequest.getContentId());
		try {
			log.info("Recovering MediaPipelineFactory");
			MediaPipelineFactory mpf = playRequest.getMediaPipelineFactory();
			log.info("Creating MediaPipeline");
			MediaPipeline mp = mpf.createMediaPipeline();
			((PlayRequestImpl) playRequest).addForCleanUp(mp);
			log.info("Creating PlayerEndPoint");
			PlayerEndPoint playerEndPoint = mp.createUriEndPoint(
					PlayerEndPoint.class, "file:///opt/video/fiwarecut.webm");
			log.info("Creating JackVaderFilter");
			JackVaderFilter filter = mp.createFilter(JackVaderFilter.class);
			log.info("Connecting " + playerEndPoint + " to " + filter);
			playerEndPoint
					.getMediaSrcs(MediaType.VIDEO)
					.iterator()
					.next()
					.connect(
							filter.getMediaSinks(MediaType.VIDEO).iterator()
									.next());
			log.info("Invoking use player");
			playRequest.usePlayer(playerEndPoint);
			log.info("Invoking play on " + filter);
			playRequest.play(filter);
			log.info("Exiting onPlayRequest");
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			playRequest.reject(500, t.getMessage());
		}
	
	}

	@Override
	public void onContentPlayed(PlayRequest playRequest) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onContentError(PlayRequest playRequest,
			ContentException exception) {
		// TODO Auto-generated method stub
		
	}

}
