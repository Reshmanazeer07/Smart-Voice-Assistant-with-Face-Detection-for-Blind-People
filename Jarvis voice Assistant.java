// JarvisAssistant.java
import java.awt.Desktop;
import java.net.URI;

public class JarvisAssistant {
    public static void main(String[] args) {
        SpeechHandler speech = new SpeechHandler();
        CommandProcessor processor = new CommandProcessor(speech);

        speech.speak("Initializing Jarvis. Systems online.");

        while (true) {
            String command = processor.listen();
            if (command == null || command.isEmpty()) continue;

            String response = processor.processCommand(command);
            speech.speak(response);

            if (command.contains("exit") || command.contains("quit") || command.contains("goodbye")) {
                break;
            }
        }
    }
}

// SpeechHandler.java
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class SpeechHandler {
    private Voice voice;

    public SpeechHandler() {
        System.setProperty("freetts.voices",
            "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager vm = VoiceManager.getInstance();
        voice = vm.getVoice("kevin16");
        if (voice != null) {
            voice.allocate();
        } else {
            System.out.println("Voice not found");
        }
    }

    public void speak(String text) {
        System.out.println("Jarvis: " + text);
        if (voice != null) {
            voice.speak(text);
        }
    }
}

// CommandProcessor.java
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class CommandProcessor {
    private SpeechHandler speech;

    public CommandProcessor(SpeechHandler speech) {
        this.speech = speech;
    }

    public String listen() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("You: ");
        return scanner.nextLine().toLowerCase();
    }

    public String processCommand(String command) {
        try {
            if (command.contains("open youtube")) {
                Desktop.getDesktop().browse(new URI("https://www.youtube.com"));
                return "Opening YouTube.";
            } else if (command.contains("what is the time")) {
                return java.time.LocalTime.now().withNano(0).toString();
            } else if (command.contains("search google for")) {
                String query = command.replace("search google for", "").trim();
                Desktop.getDesktop().browse(new URI("https://www.google.com/search?q=" + query.replace(" ", "+")));
                return "Searching Google for " + query;
            } else if (command.contains("search wikipedia for")) {
                String query = command.replace("search wikipedia for", "").trim();
                Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/" + query.replace(" ", "_")));
                return "Searching Wikipedia for " + query;
            } else if (command.contains("detect face")) {
                FaceDetection.detectFace();
                return "Face detection complete.";
            } else if (command.contains("exit") || command.contains("quit") || command.contains("goodbye")) {
                return "Goodbye! Have a great day.";
            }
        } catch (IOException | URISyntaxException e) {
            return "An error occurred: " + e.getMessage();
        }
        return "I didn't catch that. Can you repeat?";
    }
}

// FaceDetection.java
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class FaceDetection {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void detectFace() {
        CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_frontalface_default.xml");
        VideoCapture camera = new VideoCapture(0);
        Mat frame = new Mat();

        if (!camera.isOpened()) {
            System.out.println("Error: Camera not available");
            return;
        }

        int frameCount = 0;
        while (frameCount < 100) {
            camera.read(frame);
            if (!frame.empty()) {
                MatOfRect faces = new MatOfRect();
                faceDetector.detectMultiScale(frame, faces);

                for (Rect rect : faces.toArray()) {
                    Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);
                }
                Imgcodecs.imwrite("face_detect_output.jpg", frame);
                frameCount++;
            }
        }
        camera.release();
        System.out.println("Face detection image saved to face_detect_output.jpg");
    }
}