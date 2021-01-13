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
package viewer.graphmlcompatibility.extensions.for2x;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.Pen;
import javafx.scene.paint.Paint;

/**
 * Handles the yFiles for JavaFX 2.0.x version of GraphML element
 * <code>&lt;GeneralPathNodeStyle&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class ShapeNodeStyleExtension extends MarkupExtension {
  private Paint paint;
  private Pen pen;
  private ShapeNodeShape shape;

  public ShapeNodeStyleExtension() {
    ShapeNodeStyle prototype = new ShapeNodeStyle();
    paint = prototype.getPaint();
    pen = prototype.getPen();
    shape = prototype.getShape();
  }

  /**
   * Handles the GraphML alias <code>Brush</code> used in yFiles for
   * JavaFX 2.0.x for property <code>Paint</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getPaint()
   */
  public Paint getBrush() {
    return getPaint();
  }

  /**
   * Handles the GraphML alias <code>Brush</code> used in yFiles for
   * JavaFX 2.0.x for property <code>Paint</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setPaint(Paint)
   */
  public void setBrush( Paint value ) {
    setPaint(value);
  }

  public Paint getPaint() {
    return paint;
  }

  public void setPaint( Paint value ) {
    this.paint = value;
  }

  public Pen getPen() {
    return pen;
  }

  public void setPen( Pen value ) {
    this.pen = value;
  }

  public ShapeNodeShape getShape() {
    return shape;
  }

  public void setShape( ShapeNodeShape value ) {
    this.shape = value;
  }

  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    ShapeNodeStyle style = new ShapeNodeStyle();
    style.setPaint(getPaint());
    style.setPen(getPen());
    style.setShape(getShape());
    return style;
  }
}
