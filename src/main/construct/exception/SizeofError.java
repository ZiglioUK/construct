package construct.exception;

public class SizeofError extends RuntimeException{
  public SizeofError( String msg )
  {
    super(msg);
  }

  public SizeofError( Exception e )
  {
    super(e);
  }

}
