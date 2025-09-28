namespace Messenger
{
	public class Users
	{
		public string username { get; set; }
		public string email { get; set; }
		public string password { get; set; }
		public string nickname { get; set; }
		public string avatar { get; set; }

		public static byte EqualCheck(Users user1, Users user2)
		{
			if (user1.username == user2.username && user1.password != user2.password) { return 1; }

			return 2;
		}
	}
}
