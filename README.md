# SmartSpendMax

## Inspiration
In today's climate of rising inflation, where managing the increasing cost of living has become a challenge, fostering financial awareness and cultivating effective money management habits are crucial. However, the task of tracking daily expenses can often feel overwhelming. This prompted the creation of SmartSpendMax – a solution aimed at simplifying this process. SmartSpendMax provides the necessary tools to take control of spending, maximize the value of every dollar, and effortlessly track daily expenses within your budget. Leveraging Gemini AI's multimodel ability to convert audio or invoice photos into inputs and automatically generate transaction details, SmartSpendMax streamlines the record-keeping process seamlessly. With SmartSpendMax, you can put an end to procrastination and regain complete control of your financial life today.

## What it does
SmartSpendMax is an app designed to simplify the process of budgeting and expense tracking. It's tailored for users who find managing finances overwhelming or technologically challenging. Here are the key features and functionalities of SmartSpendMax:

**User-Friendly Interface**: The app provides an intuitive interface that offers a clear overview of monthly expenses at a glance, making it easier for users to understand their spending patterns.
**Customization and Categorization**: Users can customize and categorize their spending to see precisely where their money goes, whether it's for groceries, utilities, or entertainment, thus keeping their finances organized.
**Detailed Tracking**: The app allows users to track their spending in detail with just a few taps, providing the ability to monitor itemized expenses anytime and anywhere.
**Smart Input Methods**: Powered by the Gemini AI Engine, SmartSpendMax offers innovative input methods that go beyond traditional manual entry. Users can speak their expenses or upload a photo of a receipt, and the AI will automatically fill out transaction details, predict the appropriate category, and complete the entry, saving time and ensuring the accuracy and timeliness of the spending log.
**Invoice Processing**: For those who prefer to record expenses via invoices, SmartSpendMax simplifies this process as well. The app can read invoices from various sources—whether they are captured directly, selected from a photo gallery, or accessed from the cloud. Users simply confirm their selection, and the information is transferred to the expense form automatically.

## How we built it
Our project was constructed upon the foundation of a group project initially developed for our Mobile Application course. Extensive additions and modifications were implemented to incorporate features driven by Gemini AI. Utilizing Android Studio and Java, we undertook app development, ensuring a seamless user experience. We designed an intuitive app interface using Figma, adhering strictly to Google’s material design principles. Integration of audio input, camera input, photo gallery input, and URL input was seamlessly executed. Moreover, we harnessed the capabilities of the Gemini AI’s multimodel to generate transaction details from various input formats and utilized the Gemini AI to accurately predict the most suitable category.

To streamline our workflow and maintain code efficiency, we adopted Agile methodologies, facilitating regular communication and quick iterations. Continuous integration and automated testing were pivotal, ensuring code stability and minimizing errors. By employing modular design principles, we compartmentalized functionalities, enabling parallel development and reducing dependencies. Additionally, thorough documentation and code reviews promoted code transparency, facilitating seamless collaboration and knowledge sharing within the team.

## Challenges we ran into
This project is our first foray into Android development, marking a significant learning curve for our team as we rapidly expand our expertise in this domain. One challenge we encountered was understanding and addressing potential security issues within the project. Safeguarding sensitive information, such as API keys, presented a challenge; we implemented strategies like hiding the API key to mitigate these risks effectively. 
Additionally, leveraging Gemini AI while asking the right questions alongside audio or photo inputs from users, thus utilizing multimodal capabilities, proved to be challenging. Processing responses from Gemini AI and considering all possible corner cases to accurately fill in transaction details also posed a significant challenge. Despite these obstacles, we successfully navigated through them to ensure the project's success.

## Accomplishments that we're proud of
SMARTSPENDMAX itself is our biggest accomplishment.
Throughout the entire development process, starting from researching products and building prototypes, our product has undergone numerous iterations and ideation collisions to finally form this version. It deepened our understanding of the complexities involved in mobile app development. Each team member contributed their strengths and a passion for innovation, and we all cherish the learning experiences gained. We are also proud to have integrated the app with Gemini AI, transitioning from AI users to developers. Looking forward, we hope to explore more deeply in the field of AI-powered technologies.

## What we learned
Grasping the lifecycle of the app, including the Activity and Fragment life cycles, proved indispensable for mobile development, given the memory and functional constraints of mobile devices. 
We also learned about the interaction and integration between the app and Google Firebase, emphasizing the importance of utilizing NonSQL database design to enhance read/write efficiency. 
Significant effort was devoted to the UI/UX design, ensuring readability of all elements and smooth transitions between fragments, with corresponding updates reflected in the menu bars.
Understanding how to formulate precise queries to Gemini AI was crucial, as was effectively processing responses from Gemini AI to extract maximum value from its answers.
Last but not least, one lesson we learned is the importance of maintaining open communication within the team and remaining receptive to feedback. We discovered that establishing workflow procedures that prioritize efficiency can significantly enhance productivity. 

## What's next for SmartSpendMax
Our next milestone is the official launch on the app store, marking a significant step forward in our journey. As we explore the untapped potential of AI, we envision developing a personalized finance assistance tool tailored to each user's habits and preferences. Our overarching goal remains to simplify the spending process, making financial management more accessible and intuitive for all users. Moreover, we aspire to leverage AI to assist minority groups, such as individuals with cognitive impairments or the elderly, ensuring inclusivity and accessibility in our app's functionality.
