using System;
using System.Data;
using System.Security.Cryptography;
using System.Windows;
using MySql.Data.MySqlClient;

namespace EmploymentService
{
	class WorkDatabase
	{
		const string connectionString = "server=localhost;user=user;password=Secret123;database=employmentservice;port=3306";

		public static DataSet GetDataSet(string sql)
		{
			try
			{
				MySqlConnection connection = new MySqlConnection(connectionString);

				connection.Open();

				MySqlDataAdapter adapter = new MySqlDataAdapter(sql, connection);

				DataSet ds = new DataSet();

				adapter.Fill(ds);

				connection.Close();

				return ds;
			}
			catch (MySqlException ex)
			{
				MessageBox.Show(ex.ToString());

				return null;
			}
		}

		public static void ExecuteNonQuery(string sql)
		{
			try
			{
				MySqlConnection connection = new MySqlConnection(connectionString);

				connection.Open();

				MySqlCommand command = new MySqlCommand(sql, connection);

				command.ExecuteNonQuery();

				connection.Close();
			}
			catch (MySqlException ex)
			{
				MessageBox.Show(ex.ToString());
			}
		}

		public static int GetCountRows(string sql)
		{
			MySqlConnection connection = new MySqlConnection(connectionString);

			connection.Open();

			MySqlCommand command = new MySqlCommand(sql, connection);

			MySqlDataReader reader = command.ExecuteReader();

			int countRows = 0;

			while (reader.Read())
			{
				countRows++;
			}

			reader.Close();

			connection.Close();

			return countRows;
		}

		public static class CurrentUser
		{
			private static string login;
			private static string accountType;
			private static int idMatch;

			public static string Login
			{
				get { return login; }
				set
				{
					login = value;
				}
			}

			public static string AccountType
			{
				get { return accountType; }
				set
				{
					accountType = value;
				}
			}

			public static int IdMatch
			{
				get { return idMatch; }
				set
				{
					idMatch = value;
				}
			}
		}

		public static bool Authorization(string login, string password)
		{
			DataTable row = CheckUserLogin(login);

			if (row != null && row.Rows.Count != 0)
			{
				if (VerifyHashPass(row.Rows[0][2].ToString(), password))
				{
					CurrentUser.Login = row.Rows[0][1].ToString();
					CurrentUser.AccountType = row.Rows[0][3].ToString();
					CurrentUser.IdMatch = Convert.ToInt32(row.Rows[0][4]);

					return true;
				}
			}

			return false;
		}

		public static bool Registration(string login, string password, string accountType)
		{
			DataTable row = CheckUserLogin(login);

			if (row != null && row.Rows.Count == 0)
			{
				CurrentUser.Login = login;
				CurrentUser.AccountType = accountType;

				string sql;

				if (accountType != "Admin")
				{
					sql = "SELECT `AUTO_INCREMENT` FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'employmentservice' AND TABLE_NAME = ";

					string sql2 = "INSERT INTO ";

					if (accountType == "Employee")
					{
						string sql3 = "ANALYZE TABLE applicants_info;";

						ExecuteNonQuery(sql3);

						sql += "'applicants_info';";

						sql2 += "`applicants_info`(idApplicant, Surname, Name, MiddleName, Birthday, Phone) VALUES(null, 'Фамилия', 'Имя', 'Отчество', '1970-01-01', '8(123)456-78-90');";
					}
					else if (accountType == "Organization")
					{
						string sql3 = "ANALYZE TABLE organizations;";

						ExecuteNonQuery(sql3);

						sql += "'organizations';";
						
						sql2 += "`organizations`(idOrganization, Organization, FoundationDate) VALUES(null, 'Название', '1970-01-01');";
					}

					DataTable autoIncrement = CheckDataSet(GetDataSet(sql));

					if (autoIncrement != null && autoIncrement.Rows.Count != 0)
					{
						CurrentUser.IdMatch = Convert.ToInt32(autoIncrement.Rows[0][0]);
					}

					ExecuteNonQuery(sql2);
				}

				sql = "INSERT INTO `users` VALUES(null, '" + login + "', '" + HashPassword(password) + "', '" + accountType + "', '" + CurrentUser.IdMatch + "');";

				ExecuteNonQuery(sql);
				
				return true;
			}

			return false;
		}

		public static void ChangePassword(string newPassword)
		{
			string sql = "UPDATE users SET Password = '" + HashPassword(newPassword) + "' WHERE Login = '" + CurrentUser.Login + "';";

			ExecuteNonQuery(sql);
		}

		static DataTable CheckUserLogin(string login)
		{
			string sql = "SELECT * FROM `users` WHERE Login = '" + login + "';";

			return CheckDataSet(GetDataSet(sql));
		}

		public static DataTable CheckDataSet(DataSet ds)
		{
			if (ds != null)
			{
				return ds.Tables[0];
			}

			return null;
		}

		public static bool CheckVacancyApplicationStatus(int idApplicant, int idVacancy)
		{
			string sql = "SELECT * FROM vacancies_applications WHERE idApplicant = '" + idApplicant + "' AND idVacancy = '" + idVacancy + "';";

			DataTable row = CheckDataSet(GetDataSet(sql));

			if (row.Rows.Count != 0)
			{
				return true;
			}

			return false;
		}

		public static bool ChangeVacancyApplicationStatus(int idApplicant, int idVacancy)
		{
			string sql;

			if (CheckVacancyApplicationStatus(idApplicant, idVacancy))
			{
				sql = "CALL deleteApplication_vacancies_applications(" + idApplicant + ", " + idVacancy + ");";

				ExecuteNonQuery(sql);

				return false;
			}
			else
			{
				sql = "INSERT INTO `vacancies_applications` VALUES(" + idApplicant + ", " + idVacancy + ", CURDATE());";

				ExecuteNonQuery(sql);

				return true;
			}
		}
		
		public static void DeleteAccount()
		{
			string sql = null;

			if (CurrentUser.AccountType == "Employee")
			{
				sql = "DELETE FROM vacancies_applications WHERE idApplicant = " + CurrentUser.IdMatch + ";";

				ExecuteNonQuery(sql);

				sql = "DELETE FROM applicants_educations WHERE idApplicant = " + CurrentUser.IdMatch + ";";

				ExecuteNonQuery(sql);

				sql = "DELETE FROM work_exp WHERE idApplicant = " + CurrentUser.IdMatch + ";";

				ExecuteNonQuery(sql);

				sql = "DELETE FROM applicants_info WHERE idApplicant = " + CurrentUser.IdMatch + ";";

				ExecuteNonQuery(sql);
			}
			else if (CurrentUser.AccountType == "Organization")
			{
				sql = "DELETE vacancies_applications FROM vacancies_applications INNER JOIN vacancies ON vacancies.idVacancy = vacancies_applications.idVacancy WHERE idOrganization = " + CurrentUser.IdMatch + ";";

				ExecuteNonQuery(sql);

				sql = "DELETE FROM vacancies WHERE idOrganization = " + CurrentUser.IdMatch + ";";

				ExecuteNonQuery(sql);

				sql = "DELETE FROM organizations WHERE idOrganization = " + CurrentUser.IdMatch + ";";

				ExecuteNonQuery(sql);
			}

			sql = "DELETE FROM users WHERE Login = '" + CurrentUser.Login + "';";

			ExecuteNonQuery(sql);
		}

		static string HashPassword(string password)
		{
			byte[] salt, buffer;

			using (Rfc2898DeriveBytes bytes = new Rfc2898DeriveBytes(password, 0x10, 0x3e8))
			{
				salt = bytes.Salt;
				buffer = bytes.GetBytes(0x20);
			}

			byte[] dst = new byte[0x31];

			Buffer.BlockCopy(salt, 0, dst, 1, 0x10);
			Buffer.BlockCopy(buffer, 0, dst, 0x11, 0x20);

			return Convert.ToBase64String(dst);
		}

		static bool VerifyHashPass(string hashPass, string pass)
		{
			byte[] buffer;

			if (hashPass == null || pass == null)
			{
				return false;
			}

			byte[] src = Convert.FromBase64String(hashPass);

			if ((src.Length != 0x31) || (src[0] != 0))
			{
				return false;
			}

			byte[] dst = new byte[0x10], buffer2 = new byte[0x20];

			Buffer.BlockCopy(src, 1, dst, 0, 0x10);
			Buffer.BlockCopy(src, 0x11, buffer2, 0, 0x20);

			using (Rfc2898DeriveBytes bytes = new Rfc2898DeriveBytes(pass, dst, 0x3e8))
			{
				buffer = bytes.GetBytes(0x20);
			}

			return ByteArraysEqual(buffer, buffer2);
		}

		static bool ByteArraysEqual(byte[] b1, byte[] b2)
		{
			if (b1 == b2) return true;

			if (b1 == null || b2 == null) return false;

			if (b1.Length != b2.Length) return false;

			for (int i = 0; i < b1.Length; i++)
			{
				if (b1[i] != b2[i]) return false;
			}

			return true;
		}

	}
}
