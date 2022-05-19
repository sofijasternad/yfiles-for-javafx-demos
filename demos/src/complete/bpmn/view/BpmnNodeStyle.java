/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.input.INodeSizeConstraintProvider;
import com.yworks.yfiles.view.input.NodeSizeConstraintProvider;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;

/**
 * A {@link AbstractNodeStyle} implementation used as base class for nodes styles representing BPMN elements.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class BpmnNodeStyle extends AbstractNodeStyle {

  private SizeD minimumSize = SizeD.EMPTY;

  /**
   * Gets the minimum node size for nodes using this style.
   * @return The MinimumSize.
   * @see #setMinimumSize(SizeD)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "Empty", classValue = SizeD.class)
  public final SizeD getMinimumSize() {
    return this.minimumSize;
  }

  /**
   * Sets the minimum node size for nodes using this style.
   * @param value The MinimumSize to set.
   * @see #getMinimumSize()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "Empty", classValue = SizeD.class)
  public final void setMinimumSize( SizeD value ) {
    this.minimumSize = value;
  }

  private IIcon icon;

  final IIcon getIcon() {
    return this.icon;
  }

  final void setIcon( IIcon value ) {
    this.icon = value;
  }

  private int modCount;

  final int getModCount() {
    return this.modCount;
  }

  final void setModCount( int value ) {
    this.modCount = value;
  }



  final int incrementModCount() {
    ++this.modCount;
    return this.modCount;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected Node createVisual( IRenderContext context, INode node ) {
    updateIcon(node);
    if (getIcon() == null) {
      return null;
    }

    RectD bounds = node.getLayout().toRectD();
    getIcon().setBounds(new RectD(PointD.ORIGIN, bounds.toSizeD()));
    Node visual = getIcon().createVisual(context);

    VisualGroup container = new VisualGroup();
    if (visual != null) {
      container.add(visual);
    }
    container.setLayoutX(bounds.getX());
    container.setLayoutY(bounds.getY());
    container.setUserData(new IconData(modCount, bounds));
    return container;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected Node updateVisual( IRenderContext context, Node oldVisual, INode node ) {
    if (getIcon() == null) {
      return null;
    }

    VisualGroup container = (oldVisual instanceof VisualGroup) ? (VisualGroup)oldVisual : null;
    IconData cache = container != null ? (IconData) container.getUserData() : null;

    if (cache == null || cache.getModCount() != getModCount()) {
      return createVisual(context, node);
    }

    RectD newBounds = node.getLayout().toRectD();

    if (RectD.equals(cache.getBounds(), newBounds)) {
      // node bounds didn't change
      return oldVisual;
    }

    if (!SizeD.equals(cache.getBounds().getSize(), newBounds.getSize())) {
      RectD newIconBounds = new RectD(PointD.ORIGIN, newBounds.getSize());
      getIcon().setBounds(newIconBounds);

      Node oldIconVisual = null;
      Node newIconVisual = null;
      if (container.getNullableChildren().size() == 0) {
        newIconVisual = getIcon().createVisual(context);
      } else {
        oldIconVisual = container.getNullableChildren().get(0);
        newIconVisual = getIcon().updateVisual(context, oldIconVisual);
      }

      // update visual
      if (oldIconVisual != newIconVisual) {
        if (oldIconVisual != null) {
          container.remove(oldIconVisual);
        }
        if (newIconVisual != null) {
          container.add(newIconVisual);
        }
      }
    }
    container.setLayoutX(newBounds.getX());
    container.setLayoutY(newBounds.getY());
    cache.setBounds(newBounds);

    return container;
  }


  /**
   * Updates the {@link #getIcon() Icon}.
   * <p>
   * This method is called by {@link #createVisual(IRenderContext, INode)}.
   * </p>
   * @param node The node to which this style instance is assigned.
   */
  void updateIcon( INode node ) {
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected Object lookup( INode node, Class type ) {
    Object lookup = super.lookup(node, type);
    if (lookup == null && type == INodeSizeConstraintProvider.class) {
      if (!getMinimumSize().isEmpty()) {
        return new NodeSizeConstraintProvider(getMinimumSize(), SizeD.INFINITE, (IRectangle)null);
      }
    }
    return lookup;
  }

}
