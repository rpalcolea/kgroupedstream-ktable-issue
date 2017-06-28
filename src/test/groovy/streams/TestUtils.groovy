package streams

class TestUtils {
  public static String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  public static Random rand = new Random()

  static String randomString(int len = 10) {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < len; i++)
      b.append(CHARS.charAt(rand.nextInt(CHARS.length())));
    return b.toString();
  }
}
