using System;
using System.Collections.Generic;
using System.Data;
using System.Windows;
using System.Windows.Controls;

namespace EmploymentService
{
	/// <summary>
	/// Логика взаимодействия для WindowProfile.xaml
	/// </summary>
	public partial class WindowProfile : Window
	{
		DataTable eduDocuments = new DataTable();
		DataTable workExps = new DataTable();

		public WindowProfile()
		{
			InitializeComponent();

			Init();
		}

		void Init()
		{
			string accountType = WorkDatabase.CurrentUser.AccountType;

			if (accountType == "Employee")
			{
				GridEmployeeInformation.Visibility = Visibility.Visible;

				FillEmployeeInformation();

				string sql = "SELECT educational_docs.idDocNum, educational_docs.DocNum, IssueDate, InstitName, educations.Education " +
					"FROM educational_docs " +
					"INNER JOIN applicants_educations ON applicants_educations.idDocNum = educational_docs.idDocNum " +
					"INNER JOIN applicants_info ON applicants_info.idApplicant = applicants_educations.idApplicant " +
					"INNER JOIN educations ON educational_docs.idEducation = educations.idEducation " +
					"WHERE applicants_info.idApplicant = " + WorkDatabase.CurrentUser.IdMatch + ";";

				int countRows = WorkDatabase.GetCountRows(sql);

				if (countRows == 0)
				{
					GridEduDocuments.Visibility = Visibility.Hidden;
					TextBlockEduDocumentsNotExist.Visibility = Visibility.Visible;

					goto countWorkExps;
				}

				eduDocuments = WorkDatabase.CheckDataSet(WorkDatabase.GetDataSet(sql));

				ButtonNextEduDocument.Visibility = countRows > 1 ? Visibility.Visible : Visibility.Hidden;

				FillEduDocument(1);

				countWorkExps:
				sql = "SELECT * FROM work_exp WHERE idApplicant = " + WorkDatabase.CurrentUser.IdMatch + ";";

				countRows = WorkDatabase.GetCountRows(sql);

				if (countRows == 0)
				{
					GridWorkExps.Visibility = Visibility.Hidden;
					TextBlockWorkExpsNotExist.Visibility = Visibility.Visible;

					return;
				}

				workExps = WorkDatabase.CheckDataSet(WorkDatabase.GetDataSet(sql));

				ButtonNextWorkExp.Visibility = countRows > 1 ? Visibility.Visible : Visibility.Hidden;

				FillWorkExp(1);
			}
			else if (accountType == "Organization")
			{
				GridOrganization.Visibility = Visibility.Visible;
				ButtonDeleteAccount.Margin = new Thickness(203, 290, 0, 0);
				this.Height = 450;
				GridProfile.Height = 320;

				FillOrganizationInformation();
			}
			else
			{
				GridAdmin.Visibility = Visibility.Visible;
				ButtonDeleteAccount.Visibility = Visibility.Hidden;
				this.Height = 450;
				GridProfile.Height = 320;
			}
		}

		void FillEmployeeInformation()
		{
			string sql = "SELECT * FROM applicants_info WHERE idApplicant = " + WorkDatabase.CurrentUser.IdMatch + ";";

			DataTable mainInformation = WorkDatabase.CheckDataSet(WorkDatabase.GetDataSet(sql));

			TextBoxSurname.Text = mainInformation.Rows[0][1].ToString();
			TextBoxName.Text = mainInformation.Rows[0][2].ToString();
			TextBoxMiddleName.Text = mainInformation.Rows[0][3].ToString();
			TextBoxBirthday.Text = Convert.ToDateTime(mainInformation.Rows[0][4].ToString()).ToShortDateString();
			TextBoxAddress.Text = mainInformation.Rows[0][5].ToString();
			TextBoxPhone.Text = mainInformation.Rows[0][6].ToString();
		}

		void FillOrganizationInformation()
		{
			string sql = "SELECT * FROM organizations WHERE idOrganization = " + WorkDatabase.CurrentUser.IdMatch + ";";

			DataTable mainInformation = WorkDatabase.CheckDataSet(WorkDatabase.GetDataSet(sql));

			TextBoxOrganizationName.Text = mainInformation.Rows[0][1].ToString();
			TextBoxFoundationDate.Text = Convert.ToDateTime(mainInformation.Rows[0][2].ToString()).ToShortDateString();
			TextBoxOrganizationInformation.Text = mainInformation.Rows[0][3].ToString();
		}

		void FillEduDocument(int numEduDocument)
		{
			TextBlockNumEduDocument.Text = "Документ " + numEduDocument;

			TextBoxDocNum.Text = eduDocuments.Rows[numEduDocument - 1][1].ToString();
			TextBoxIssueDate.Text = Convert.ToDateTime(eduDocuments.Rows[numEduDocument - 1][2].ToString()).ToShortDateString();
			TextBoxInstitName.Text = eduDocuments.Rows[numEduDocument - 1][3].ToString();
			TextBoxEducation.Text = eduDocuments.Rows[numEduDocument - 1][4].ToString();
		}

		void FillWorkExp(int numWorkExp)
		{
			TextBlockNumWorkExp.Text = "Опыт работы " + numWorkExp;

			TextBoxPost.Text = workExps.Rows[numWorkExp - 1][3].ToString();
			TextBoxOrganization.Text = workExps.Rows[numWorkExp - 1][4].ToString();
			TextBoxStartDate.Text = Convert.ToDateTime(workExps.Rows[numWorkExp - 1][1].ToString()).ToShortDateString();
			TextBoxEndDate.Text = Convert.ToDateTime(workExps.Rows[numWorkExp - 1][2].ToString()).ToShortDateString();
			TextBoxShortDescription.Text = workExps.Rows[numWorkExp - 1][5].ToString();
		}

		int GetCurrentEduDocument()
		{
			int pos = TextBlockNumEduDocument.Text.ToString().IndexOf(' ');

			return Convert.ToInt32(TextBlockNumEduDocument.Text.ToString().Substring(++pos));
		}

		int GetCurrentWorkExp()
		{
			int pos = TextBlockNumWorkExp.Text.ToString().LastIndexOf(' ');

			return Convert.ToInt32(TextBlockNumWorkExp.Text.ToString().Substring(++pos));
		}

		private void ButtonPrevEduDocument_Click(object sender, RoutedEventArgs e)
		{
			if (ButtonNextEduDocument.Visibility == Visibility.Hidden)
			{
				ButtonNextEduDocument.Visibility = Visibility.Visible;
			}

			int numEduDocument = GetCurrentEduDocument() - 1;

			if (numEduDocument == 1)
			{
				((Button)sender).Visibility = Visibility.Hidden;
			}

			FillEduDocument(numEduDocument);
		}

		private void ButtonNextEduDocument_Click(object sender, RoutedEventArgs e)
		{
			if (ButtonPrevEduDocument.Visibility == Visibility.Hidden)
			{
				ButtonPrevEduDocument.Visibility = Visibility.Visible;
			}

			int numEduDocument = GetCurrentEduDocument() + 1;

			string sql = "SELECT educational_docs.idDocNum, educational_docs.DocNum, IssueDate, InstitName, educations.Education " +
					"FROM educational_docs " +
					"INNER JOIN applicants_educations ON applicants_educations.idDocNum = educational_docs.idDocNum " +
					"INNER JOIN applicants_info ON applicants_info.idApplicant = applicants_educations.idApplicant " +
					"INNER JOIN educations ON educational_docs.idEducation = educations.idEducation " +
					"WHERE applicants_info.idApplicant = " + WorkDatabase.CurrentUser.IdMatch + ";";

			int countEduDocuments = WorkDatabase.GetCountRows(sql);

			if (numEduDocument == countEduDocuments)
			{
				((Button)sender).Visibility = Visibility.Hidden;
			}

			FillEduDocument(numEduDocument);
		}

		private void ButtonPrevWorkExp_Click(object sender, RoutedEventArgs e)
		{
			if (ButtonNextWorkExp.Visibility == Visibility.Hidden)
			{
				ButtonNextWorkExp.Visibility = Visibility.Visible;
			}

			int numWorkExp = GetCurrentWorkExp() - 1;

			if (numWorkExp == 1)
			{
				((Button)sender).Visibility = Visibility.Hidden;
			}

			FillWorkExp(numWorkExp);
		}

		private void ButtonNextWorkExp_Click(object sender, RoutedEventArgs e)
		{
			if (ButtonPrevWorkExp.Visibility == Visibility.Hidden)
			{
				ButtonPrevWorkExp.Visibility = Visibility.Visible;
			}

			int numWorkExp = GetCurrentWorkExp() + 1;

			string sql = "SELECT * FROM work_exp WHERE idApplicant = " + WorkDatabase.CurrentUser.IdMatch + ";";

			int countWorkExps = WorkDatabase.GetCountRows(sql);

			if (numWorkExp == countWorkExps)
			{
				((Button)sender).Visibility = Visibility.Hidden;
			}

			FillWorkExp(numWorkExp);
		}

		private void ButtonBack_Click(object sender, RoutedEventArgs e)
		{
			WindowMainMenu windowMainMenu = new WindowMainMenu();

			windowMainMenu.Show();

			this.Close();
		}

		private void ButtonDeleteAccount_Click(object sender, RoutedEventArgs e)
		{
			if (MessageBox.Show("Вы точно хотите удалить аккаунт? Вся информация будет утрачена", "Предупреждение", MessageBoxButton.YesNo, MessageBoxImage.Warning) == MessageBoxResult.Yes)
			{
				WorkDatabase.DeleteAccount();

				MessageBox.Show("Ваш аккаунт был удален");

				WindowAuthorization windowAuthorization = new WindowAuthorization();

				windowAuthorization.Show();

				this.Close();
			}
		}

		private void ButtonEmployeeInformationEdit_Click(object sender, RoutedEventArgs e)
		{
			TextBoxSurname.IsReadOnly = false;
			TextBoxName.IsReadOnly = false;
			TextBoxMiddleName.IsReadOnly = false;
			TextBoxBirthday.IsReadOnly = false;
			TextBoxAddress.IsReadOnly = false;
			TextBoxPhone.IsReadOnly = false;

			((Button)sender).Visibility = Visibility.Hidden;
			ButtonEmployeeInformationEditSave.Visibility = Visibility.Visible;
			ButtonEmployeeInformationEditCancel.Visibility = Visibility.Visible;
		}

		private void ButtonEmployeeInformationEditSave_Click(object sender, RoutedEventArgs e)
		{
			string sql = "UPDATE applicants_info SET ";

			List<string> changes = new List<string>();

			if (!string.IsNullOrWhiteSpace(TextBoxSurname.Text))
			{
				changes.Add("Surname = '" + TextBoxSurname.Text + "'");
			}

			if (!string.IsNullOrWhiteSpace(TextBoxName.Text))
			{
				changes.Add("Name = '" + TextBoxName.Text + "'");
			}

			if (!string.IsNullOrWhiteSpace(TextBoxMiddleName.Text))
			{
				changes.Add("MiddleName = '" + TextBoxMiddleName.Text + "'");
			}

			if (!string.IsNullOrWhiteSpace(TextBoxBirthday.Text))
			{
				changes.Add("Birthday = '" + Convert.ToDateTime(TextBoxBirthday.Text).ToString("yyyy-MM-dd") + "'");
			}

			if (!string.IsNullOrWhiteSpace(TextBoxAddress.Text))
			{
				changes.Add("Address = '" + TextBoxAddress.Text + "'");
			}

			if (!string.IsNullOrWhiteSpace(TextBoxPhone.Text))
			{
				changes.Add("Phone = '" + TextBoxPhone.Text + "'");
			}

			if (changes.Count > 0)
			{
				sql += string.Join(", ", changes);

				sql += " WHERE idApplicant = " + WorkDatabase.CurrentUser.IdMatch + ";";

				WorkDatabase.ExecuteNonQuery(sql);
			}

			TextBoxSurname.IsReadOnly = true;
			TextBoxName.IsReadOnly = true;
			TextBoxMiddleName.IsReadOnly = true;
			TextBoxBirthday.IsReadOnly = true;
			TextBoxAddress.IsReadOnly = true;
			TextBoxPhone.IsReadOnly = true;

			FillEmployeeInformation();

			ButtonEmployeeInformationEdit.Visibility = Visibility.Visible;
			((Button)sender).Visibility = Visibility.Hidden;
			ButtonEmployeeInformationEditCancel.Visibility = Visibility.Hidden;
		}

		private void ButtonEmployeeInformationEditCancel_Click(object sender, RoutedEventArgs e)
		{
			TextBoxSurname.IsReadOnly = true;
			TextBoxName.IsReadOnly = true;
			TextBoxMiddleName.IsReadOnly = true;
			TextBoxBirthday.IsReadOnly = true;
			TextBoxAddress.IsReadOnly = true;
			TextBoxPhone.IsReadOnly = true;

			FillEmployeeInformation();

			ButtonEmployeeInformationEdit.Visibility = Visibility.Visible;
			ButtonEmployeeInformationEditSave.Visibility = Visibility.Hidden;
			((Button)sender).Visibility = Visibility.Hidden;
		}

		private void ButtonOrganizationInformationEdit_Click(object sender, RoutedEventArgs e)
		{
			TextBoxOrganizationName.IsReadOnly = false;
			TextBoxFoundationDate.IsReadOnly = false;
			TextBoxOrganizationInformation.IsReadOnly = false;

			((Button)sender).Visibility = Visibility.Hidden;
			ButtonOrganizationInformationEditSave.Visibility = Visibility.Visible;
			ButtonOrganizationInformationEditCancel.Visibility = Visibility.Visible;
		}

		private void ButtonOrganizationInformationEditSave_Click(object sender, RoutedEventArgs e)
		{
			string sql = "UPDATE organizations SET ";

			List<string> changes = new List<string>();

			if (!string.IsNullOrWhiteSpace(TextBoxOrganizationName.Text))
			{
				changes.Add("Organization = '" + TextBoxOrganizationName.Text + "'");
			}

			if (!string.IsNullOrWhiteSpace(TextBoxFoundationDate.Text))
			{
				changes.Add("FoundationDate = '" + Convert.ToDateTime(TextBoxFoundationDate.Text).ToString("yyyy-MM-dd") + "'");
			}

			if (!string.IsNullOrWhiteSpace(TextBoxOrganizationInformation.Text))
			{
				changes.Add("Information = '" + TextBoxOrganizationInformation.Text + "'");
			}

			if (changes.Count > 0)
			{
				sql += string.Join(", ", changes);

				sql += " WHERE idOrganization = " + WorkDatabase.CurrentUser.IdMatch + ";";

				WorkDatabase.ExecuteNonQuery(sql);
			}

			TextBoxOrganizationName.IsReadOnly = true;
			TextBoxFoundationDate.IsReadOnly = true;
			TextBoxOrganizationInformation.IsReadOnly = true;
			
			FillOrganizationInformation();

			ButtonOrganizationInformationEdit.Visibility = Visibility.Visible;
			((Button)sender).Visibility = Visibility.Hidden;
			ButtonOrganizationInformationEditCancel.Visibility = Visibility.Hidden;
		}

		private void ButtonOrganizationInformationEditCancel_Click(object sender, RoutedEventArgs e)
		{
			TextBoxOrganizationName.IsReadOnly = true;
			TextBoxFoundationDate.IsReadOnly = true;
			TextBoxOrganizationInformation.IsReadOnly = true;

			FillOrganizationInformation();

			ButtonOrganizationInformationEdit.Visibility = Visibility.Visible;
			ButtonOrganizationInformationEditSave.Visibility = Visibility.Hidden;
			((Button)sender).Visibility = Visibility.Hidden;
		}
	}
}
