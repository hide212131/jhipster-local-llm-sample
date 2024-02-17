package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UploadedFileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UploadedFile getUploadedFileSample1() {
        return new UploadedFile().id(1L).fileId(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa")).filename("filename1");
    }

    public static UploadedFile getUploadedFileSample2() {
        return new UploadedFile().id(2L).fileId(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367")).filename("filename2");
    }

    public static UploadedFile getUploadedFileRandomSampleGenerator() {
        return new UploadedFile().id(longCount.incrementAndGet()).fileId(UUID.randomUUID()).filename(UUID.randomUUID().toString());
    }
}
