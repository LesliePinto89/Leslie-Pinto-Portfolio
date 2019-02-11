package midiDevices;

import java.util.ArrayList;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import tools.MIDIRecord;

public class MidiReciever implements Receiver {
	//static Timer timer;
	//static Timer timerSustain;
	//private double ticksPerSecond;
	//private double tickSize;
	//private int restResolution = 0;
	//private boolean endSustainCycle = false;
	
	private MidiDevice device;
	private Sequencer sequencer;
	private Transmitter seqTrans;
	private Receiver seqRcvr;
	private Receiver synthRcvr;
	private boolean stopRecording = true;
	private boolean stopPianoFreePlay = false;
	private boolean firstRecording = true;
	private Sequence sequence;
	private int resolution;
	private Track track;
	private int sequenceCounter = 0;

	private MIDIRecord carriedRecord;
	

	//A singleton pattern so that only one instance of this class 
	//can be accessed and instantiated
	private static volatile MidiReciever instance = null;

    private MidiReciever() {}

    public static MidiReciever getInstance() {
        if (instance == null) {
            synchronized(MidiReciever.class) {
                if (instance == null) {
                    instance = new MidiReciever();
                }
            }
        }

        return instance;
    }
	
	public void startConnection() throws InvalidMidiDataException, MidiUnavailableException {
		ArrayList<MidiDevice.Info> synthInfos = new ArrayList<MidiDevice.Info>();
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
			device = MidiSystem.getMidiDevice(infos[i]);
			if (device instanceof Synthesizer) {
				synthInfos.add(infos[i]);
			}
		}
		device = MidiSystem.getMidiDevice(synthInfos.get(0));
		loadUp();
	}

	public int getCurrentSequenceResolution() {
		return resolution; 
	}
	
	
	// To keep instance of record class object (Could use singleton pattern
	// alternative)
	public void storedRecordStart(MIDIRecord storedRecord) {
		carriedRecord = storedRecord;
	}

	public MIDIRecord getStoredRecordStart() {
		return carriedRecord;
	}

	public void loadUp() throws MidiUnavailableException {
		if (!(device.isOpen())) {
			device.open();
		}
		synthRcvr = device.getReceiver();
		sequencer = MidiSystem.getSequencer();
		seqRcvr = sequencer.getReceiver();
		seqTrans = sequencer.getTransmitter();
		seqTrans.setReceiver(synthRcvr);
		sequencer.open();
		storeDevice(device);
	}

	public void storeTrack(Track track) {
		this.track = track;
	}

	public Track getTrack() {
		return track;
	}

	public void setSequenceCounter(int seqCounter) {
		sequenceCounter = seqCounter;
	}

	public int getSequenceCounter() {
		return sequenceCounter;
	}

	public void storeSeq(Sequence sequence) {
		this.sequence = sequence;	
		resolution = sequence.getResolution(); // stored resolution as its base
	}

	public Sequence getSequence() {
		return sequence;
	}

	public void createNewSequence() throws InvalidMidiDataException {
		sequence = new Sequence(Sequence.PPQ, 480);
	}

	public MidiDevice returnDevice() {
		return device;
	}

	public void storeDevice(MidiDevice aDevice) {
		this.device = aDevice;
	}

	public Sequencer returnSequencer() {
		return sequencer;
	}

	public Receiver returnSequencerReceiver() {
		return seqRcvr;
	}

	public Transmitter returnSeqTransmitter() {
		return seqTrans;
	}

	@Override
	public void close() {
	}

	//Handles MIDI wire protocol play back
	@Override
	public void send(MidiMessage message, long timeStamp) {
		synthRcvr.send(message, timeStamp);

	}
	public void freeNotePlay(int pitch) throws InvalidMidiDataException {
		ShortMessage noteOnMessage = new ShortMessage(ShortMessage.NOTE_ON, 0, pitch, 100);
		send(noteOnMessage, -1);
		System.out.println("It has reached here");

	}

	public void freeNoteStop(int pitch) throws InvalidMidiDataException {
		//Changed from note off, 100 velocity to note on 0 velocity for testing purposes
		ShortMessage noteOnMessage = new ShortMessage(ShortMessage.NOTE_ON, 0, pitch, 0);
		send(noteOnMessage, -1);
	}

	// CONDITION VARIABLES/////////////////////////////////
	public void setFirstRecording(boolean firstRec) {
		this.firstRecording = firstRec;
	}

	public boolean getFirstRecording() {
		return firstRecording;
	}

	public void endRecording(boolean stopRec) {
		this.stopRecording = stopRec;
	}

	public boolean isRecEnded() {
		return stopRecording;
	}

	public void endFreePlay(boolean stopFreePlay) {
		this.stopPianoFreePlay = stopFreePlay;
	}

	public boolean isFreePlayEnded() {
		return stopPianoFreePlay;
	}

	public boolean isRunning() {
		return sequencer.isRunning();
	}
}