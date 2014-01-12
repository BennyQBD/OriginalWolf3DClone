package com.base.engine;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class AudioUtil
{
	private static Sequencer sequencer;
	private static final float AUDIO_VOLUME = -5.0f;
	private static final float DECAY_FACTOR = 0.12f;
	
	public static void playAudio(Clip clip, float distance)
	{
//		FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
//
//		float volumeAmount = AUDIO_VOLUME - (distance * distance * DECAY_FACTOR);
//
//		if(volumeAmount < -80)
//			volumeAmount = -80;
//
//		volume.setValue(volumeAmount);
//
//		if(clip.isRunning())
//			clip.stop();
//
//		clip.setFramePosition(0);
//		clip.start();
	}
	
	public static void playMidi(Sequence midi)
	{
		try
		{
			if(sequencer == null)
				sequencer = MidiSystem.getSequencer();
	        if(sequencer.isOpen())
	        {
	        	sequencer.stop();
	        	sequencer.setTickPosition(0);
	        }
	        
	        if(midi == null)
			{
	        	sequencer.stop();
				sequencer.close();
				return;
			}
	        
	        sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
	        //sequencer.setLoopCount(0);
	        sequencer.setSequence(midi);
	        sequencer.open();
	
	        sequencer.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean isPlayingMidi()
	{
		return sequencer.isRunning();
	}
}
