# What is this project?

This project contains the tutorials and code for [Pilecv4j](https://github.com/KognitionAI/pilecv4j). If the documentation mentions and example or tutorial, the code for that example or tutorial should be here.

- [What is this project?](#what-is-this-project)
- [The Tutorials](#the-tutorials)
- [Philly JUG Examples.](#philly-jug-examples)
- [Tutorial 0 - Simple Example](#tutorial-0---simple-example)
  - [Using the webcam in the example.](#using-the-webcam-in-the-example)
  - [Using an RTSP camera.](#using-an-rtsp-camera)

# The Tutorials

Each of these tutorials is *NEARLY* standalone, however, most require a test video or a camera feed which you'll need to supply. Appart from that they are fully working codebases and as long as you have the prerequisites installed (see [Pilecv4j Prerequisites](https://github.com/KognitionAI/pilecv4j#prerequisites)), and supply your own test videos, everything should run. Please feel free to submit an issue if this ever isn't the case.


# Philly JUG Examples.

**Note:** To get all of these working you need to supply a test-video and have access to a live IP camera. You can change how these are defined by changing the following lines in the `BaseTest` class. 

```java
    public static final String VIDEO = "rtsp://admin:password@172.16.2.11:554/";
    public static final String VIDEO_FILE = "/tmp/test-video.mp4";
```

**Note:** These are all JUnit tests and as you can see in the presentation linked below, are meant to be played with from an IDE capable of running individual tests on the fly.

The projects in the subdirectory `philly-jug-examples` are the working code from the introduction and overview of the project that was presented to the Philly Java Users Group on Feb 22, 2023. You can find a video of the presentation here: 

[![Image and Video Processing using OpenCV and Pilecv4j](https://img.youtube.com/vi/FrSOjOil1o8/1.jpg)](https://www.youtube.com/watch?v=FrSOjOil1o8). 

All of the examples that were shown in the presentation are available in this subdirectory.

# Tutorial 0 - Simple Example

The project in the subdirectory "tutorial-0-simple-example" is an *almost*-standalone fully working codebase for the *Short Example* mentioned in the *Jumping Right In* section of the main project's README. The only missing component is the `TEST_VIDEO`. You can drop an `mp4` file at `/tmp/test-video.mp4` or you can change the `TEST_VIDEO` variable to point to a location of your video to get the example working.

## Using the webcam in the example.

If you want to use your webcam, and you're on Linux, you can change the line that creates the `MediaConext` to read:

```java
            final MediaContext sctx = Ffmpeg.createMediaContext("v4l2", "/dev/video0")
```

or, if your camera isn't `/dev/video0`, whatever device your camera is on.

## Using an RTSP camera.

You can pass an RTSP URL to the `Ffmpeg.createMediaContext` also.


