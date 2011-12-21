package construct.exception;

public class ValueError extends RuntimeException{
  public ValueError( String msg )
  {
    super(msg);
  }

  public ValueError( Exception e )
  {
    super(e);
  }

}
