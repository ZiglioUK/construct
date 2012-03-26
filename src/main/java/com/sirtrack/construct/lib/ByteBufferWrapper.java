package com.sirtrack.construct.lib;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteBufferWrapper{

	public ByteBuffer bb;
	
  /**
   * Wraps a byte array into a buffer.
   *
   * <p> The new buffer will be backed by the given byte array;
   * that is, modifications to the buffer will cause the array to be modified
   * and vice versa.  The new buffer's capacity and limit will be
   * <tt>array.length</tt>, its position will be zero, and its mark will be
   * undefined.  Its {@link #array </code>backing array<code>} will be the
   * given array, and its {@link #arrayOffset </code>array offset<code>} will
   * be zero.  </p> 
   *
   * @param  array
   *         The array that will back this buffer
   *
   * @return  The new byte buffer
   */
  public ByteBufferWrapper wrap(byte[] array) {
  	bb = ByteBuffer.wrap(array, 0, array.length);
  	return this;
  }

  /**
   * Returns the number of elements between the current position and the
   * limit. </p>
   *
   * @return  The number of elements remaining in this buffer
   */
  public int remaining() {
	  return bb.remaining();
  }

  /**
   * Relative <i>get</i> method.  Reads the byte at this buffer's
   * current position, and then increments the position. </p>
   *
   * @return  The byte at the buffer's current position
   *
   * @throws  BufferUnderflowException
   *          If the buffer's current position is not smaller than its limit
   */
  public byte get(){
  	return bb.get();
  }
  
  /**
   * Relative bulk <i>get</i> method.
   *
   * <p> This method transfers bytes from this buffer into the given
   * destination array.  If there are fewer bytes remaining in the
   * buffer than are required to satisfy the request, that is, if
   * <tt>length</tt>&nbsp;<tt>&gt;</tt>&nbsp;<tt>remaining()</tt>, then no
   * bytes are transferred and a {@link BufferUnderflowException} is
   * thrown.
   *
   * <p> Otherwise, this method copies <tt>length</tt> bytes from this
   * buffer into the given array, starting at the current position of this
   * buffer and at the given offset in the array.  The position of this
   * buffer is then incremented by <tt>length</tt>.
   *
   * <p> In other words, an invocation of this method of the form
   * <tt>src.get(dst,&nbsp;off,&nbsp;len)</tt> has exactly the same effect as
   * the loop
   *
   * <pre>
   *     for (int i = off; i < off + len; i++)
   *         dst[i] = src.get(); </pre>
   *
   * except that it first checks that there are sufficient bytes in
   * this buffer and it is potentially much more efficient. </p>
   *
   * @param  dst
   *         The array into which bytes are to be written
   *
   * @param  offset
   *         The offset within the array of the first byte to be
   *         written; must be non-negative and no larger than
   *         <tt>dst.length</tt>
   *
   * @param  length
   *         The maximum number of bytes to be written to the given
   *         array; must be non-negative and no larger than
   *         <tt>dst.length - offset</tt>
   *
   * @return  This buffer
   *
   * @throws  BufferUnderflowException
   *          If there are fewer than <tt>length</tt> bytes
   *          remaining in this buffer
   *
   * @throws  IndexOutOfBoundsException
   *          If the preconditions on the <tt>offset</tt> and <tt>length</tt>
   *          parameters do not hold
   */
  public ByteBufferWrapper get(byte[] dst, int offset, int length) {
	  bb.get(dst,offset,length);
	  return this;
  }

  /**
   * Returns this buffer's position. </p>
   *
   * @return  The position of this buffer
   */
	public int position() {
	  return bb.position();
  }

  /**
   * Sets this buffer's position.  If the mark is defined and larger than the
   * new position then it is discarded. </p>
   *
   * @param  newPosition
   *         The new position value; must be non-negative
   *         and no larger than the current limit
   *
   * @return  This buffer
   *
   * @throws  IllegalArgumentException
   *          If the preconditions on <tt>newPosition</tt> do not hold
   */
	public void position(int pos) {
	   bb.position(pos);
  }

}
