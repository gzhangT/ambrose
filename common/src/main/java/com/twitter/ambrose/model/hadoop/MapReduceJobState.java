package com.twitter.ambrose.model.hadoop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sun.org.apache.commons.logging.Log;

import org.apache.hadoop.mapred.JobStatus;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TIPStatus;
import org.apache.hadoop.mapred.TaskReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Container that holds state of a MapReduce job
 */
public class MapReduceJobState {
  private String jobId;
  private String jobName;
  private String trackingURL;
  private boolean isComplete;
  private boolean isSuccessful;
  private float mapProgress;
  private float reduceProgress;
  private int totalMappers;
  private int totalReducers;
  private long mapTaskStartTime;
  private long reduceTaskStartTime;
  private long mapTaskEndTime;
  private long reduceTaskEndTime;
  private int finishedMappers;
  private int finishedReducers;

  private List<Long> mapperFinishTimeList;
  private List<Long> reducerFinishTimeList;

  @JsonCreator
  public MapReduceJobState() { }

  @SuppressWarnings("deprecation")
  public MapReduceJobState(RunningJob runningJob,
                           TaskReport[] mapTaskReport,
                           TaskReport[] reduceTaskReport) throws IOException {
    jobId = runningJob.getID().toString();
    jobName = runningJob.getJobName();
    trackingURL = runningJob.getTrackingURL();
    isComplete = runningJob.isComplete();
    isSuccessful = runningJob.isSuccessful();
    mapProgress = runningJob.mapProgress();
    reduceProgress = runningJob.reduceProgress();
    totalMappers = mapTaskReport.length;
    totalReducers = reduceTaskReport.length;

    mapTaskStartTime = Long.MAX_VALUE;
    reduceTaskStartTime = Long.MAX_VALUE;
    mapTaskEndTime = 0L;
    reduceTaskEndTime = 0L;
    finishedMappers = 0;
    finishedReducers = 0;

    mapperFinishTimeList = new ArrayList<Long>();
    reducerFinishTimeList = new ArrayList<Long>();

    for (TaskReport report : mapTaskReport) {
        if (mapTaskEndTime < report.getFinishTime()) mapTaskEndTime = report.getFinishTime();
        if (report.getStartTime() < mapTaskStartTime) mapTaskStartTime = report.getStartTime();

        TIPStatus status = report.getCurrentStatus();
        if (status != TIPStatus.PENDING && status != TIPStatus.RUNNING) {
            finishedMappers++;
        }
        mapperFinishTimeList.add(report.getFinishTime());
    }

    for (TaskReport report : reduceTaskReport) {
        if (reduceTaskEndTime < report.getFinishTime()) reduceTaskEndTime = report.getFinishTime();
        if (report.getStartTime() < reduceTaskStartTime) reduceTaskStartTime = report.getStartTime();

        TIPStatus status = report.getCurrentStatus();
        if (status != TIPStatus.PENDING && status != TIPStatus.RUNNING) {
            finishedReducers++;
        }
        reducerFinishTimeList.add(report.getFinishTime());
    }
  }

  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.jobId = jobId;
  }

  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public String getTrackingURL() {
    return trackingURL;
  }

  public void setTrackingURL(String trackingURL) {
    this.trackingURL = trackingURL;
  }

  public boolean isComplete() {
    return isComplete;
  }

  public void setComplete(boolean complete) {
    isComplete = complete;
  }

  public boolean isSuccessful() {
    return isSuccessful;
  }

  public void setSuccessful(boolean successful) {
    isSuccessful = successful;
  }

  public float getMapProgress() {
    return mapProgress;
  }

  public void setMapProgress(float mapProgress) {
    this.mapProgress = mapProgress;
  }

  public float getReduceProgress() {
    return reduceProgress;
  }

  public void setReduceProgress(float reduceProgress) {
    this.reduceProgress = reduceProgress;
  }

  public int getTotalMappers() {
    return totalMappers;
  }

  public void setTotalMappers(int totalMappers) {
    this.totalMappers = totalMappers;
  }

  public int getTotalReducers() {
    return totalReducers;
  }

  public void setTotalReducers(int totalReducers) {
    this.totalReducers = totalReducers;
  }

  public int getFinishedMappers() {
      return finishedMappers;
  }

  public void setFinishedMappers(int finishedMappers) {
      this.finishedMappers = finishedMappers;
  }

  public int getFinishedReducers() {
      return finishedReducers;
  }

  public void setFinishedReducers(int finishedReducers) {
      this.finishedReducers = finishedReducers;
  }

  public long getMapTaskStartTime() {
      return mapTaskStartTime;
  }

  public void setMapTaskStartTime(long mapTaskStartTime) {
      this.mapTaskStartTime = mapTaskStartTime;
  }

  public long getReduceTaskStartTime() {
      return reduceTaskStartTime;
  }

  public void setReduceTaskStartTime(long reduceTaskStartTime) {
      this.reduceTaskStartTime = reduceTaskStartTime;
  }

  public long getMapTaskEndTime() {
      return mapTaskEndTime;
  }

  public void setMapTaskEndTime(long mapTaskEndTime) {
      this.mapTaskEndTime = mapTaskEndTime;
  }

  public long getReduceTaskEndTime() {
      return reduceTaskEndTime;
  }

  public void setReduceTaskEndTime(long reduceTaskEndTime) {
      this.reduceTaskEndTime = reduceTaskEndTime;
  }

  public List<Long> getMapperFinishTimeList() {
    return mapperFinishTimeList;
  }

  public void setMapperFinishTimeList(List<Long> mapperFinishTimeList) {
    this.mapperFinishTimeList = mapperFinishTimeList;
  }

  public List<Long> getReducerFinishTimeList() {
    return reducerFinishTimeList;
  }

  public void setReducerFinishTimeList(List<Long> reducerFinishTimeList) {
    this.reducerFinishTimeList = reducerFinishTimeList;
  }
}