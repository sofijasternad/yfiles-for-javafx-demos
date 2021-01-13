/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ** yFiles demo files exhibit yFiles for JavaFX functionalities. Any redistribution
 ** of demo files in source code or binary form, with or without
 ** modification, is not permitted.
 **
 ** Owners of a valid software license for a yFiles for JavaFX version that this
 ** demo is shipped with are allowed to use the demo source code as basis
 ** for their own yFiles for JavaFX powered applications. Use of such programs is
 ** governed by the rights and conditions as set out in the yFiles for JavaFX
 ** license agreement.
 **
 ** THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESS OR IMPLIED
 ** WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 ** NO EVENT SHALL yWorks BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 ** SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 ** TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 ** PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 ** LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 ** NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 ** SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **
 ***************************************************************************/
package viewer.ganttchart;

import java.time.LocalDateTime;

/**
 * Stores all information about an activity in the project schedule. 
 */
public class Activity {
  public final static String ORIGIN_DATE = "2021-05-21";

  private String name;
  private Task task;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private double leadTime;
  private double followUpTime;

  private int subrowIndex;


  /**
   * Initializes a new {@code Activity} instance.
   */
  public Activity() {
    this(null, null, null);
  }

  /**
   * Initializes a new {@code Activity} instance.
   * @param task The task to which this activity is assigned to.
   */
  public Activity(Task task, LocalDateTime startDate, LocalDateTime endDate) {
    this.name = "New Activity";
    this.task = task;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  /**
   * Gets the name of the activity.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name for this activity.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the task this activity is assigned to.
   */
  public Task getTask() {
    return task;
  }

  /**
   * Sets the task this activity belongs to.
   */
  public void setTask(Task task) {
    this.task = task;
  }

  /**
   * Gets the start date/time of the activity.
   */
  public LocalDateTime getStartDate() {
    return startDate;
  }

  /**
   * Sets the start date/time for this activity.
   */
  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  /**
   * Gets the  end date/time of the activity.
   */
  public LocalDateTime getEndDate() {
    return endDate;
  }

  /**
   * Sets the end date/time for this activity.
   */
  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  /**
   * Gets the lead time of the activity.
   */
  public double getLeadTime() {
    return leadTime;
  }

  /**
   * Sets the lead time for this activity.
   */
  public void setLeadTime(double leadTime) {
    this.leadTime = leadTime;
  }

  /**
   * Calculates the lead time width for this activity.
   */
  public double leadTimeWidth() {
    return GanttDataUtil.hoursToWorldLength(getLeadTime());
  }

  /**
   * Gets the follow up time of the activity.
   */
  public double getFollowUpTime() {
    return followUpTime;
  }

  /**
   * Sets the follow up time for this activity.
   */
  public void setFollowUpTime(double followUpTime) {
    this.followUpTime = followUpTime;
  }

  /**
   * Calculates the width of the follow up time for this activity.
   */
  public double followUpTimeWidth() {
    return GanttDataUtil.hoursToWorldLength(getFollowUpTime());
  }

  /**
   * Gets the index of the subrow this activity belongs to.
   */
  public int getSubrowIndex() {
    return subrowIndex;
  }

  /**
   * Sets the subrow index this activity belongs to.
   */
  public void setSubrowIndex(int subrowIndex) {
    this.subrowIndex = subrowIndex;
  }
}
