/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package viewer.printing;

import javafx.scene.control.TextField;

/**
 * A TextField for the input fields in the demo that only accepts numbers.
 */
public class NumbersOnlyTextField extends TextField {

  private boolean doubleAllowed = false;

  @Override
  public void replaceText(int start, int end, String text) {
    if (text.matches("[0-9]") || text.isEmpty() || (doubleAllowed && text.matches(".") && !getText().contains("."))) {
      super.replaceText(start, end, text);
    }
  }
  @Override

  public void replaceSelection(String text) {
    if (text.matches("[0-9]") || text.isEmpty() || (doubleAllowed && text.matches(".") && !getText().contains("."))) {
      super.replaceSelection(text);
    }
  }

  /**
   * Returns whether double values are allowed in the TextField (i.e., if a "." can be entered)
   */
  public boolean isDoubleAllowed() {
    return doubleAllowed;
  }

  /**
   * Sets whether double values are allowed in the TextField (i.e., if a "." can be entered)
   */
  public void setDoubleAllowed(final boolean doubleAllowed) {
    this.doubleAllowed = doubleAllowed;
  }
}