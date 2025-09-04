# AcademicGuard - Blockchain Academic Plagiarism Detection Platform

A modern React frontend for a blockchain-based academic plagiarism detection and research paper authentication platform. Researchers can submit papers with blockchain hashes to prove authorship and timestamp, while AI-powered systems detect plagiarism before submission.

## Features

- **Blockchain Hash Submission**: Generate unique blockchain hashes for research papers to prove authorship and timestamp
- **AI Plagiarism Detection**: Advanced AI system that scans papers against millions of academic sources
- **Paper Verification**: Verify paper authenticity using blockchain hashes or search by title/author
- **Modern UI/UX**: Beautiful, responsive design with smooth animations
- **Real-time Processing**: Live plagiarism checking and blockchain verification
- **Academic Integrity**: Ensure research papers are original and properly attributed

## Pages

1. **Home**: Hero section with platform overview and key features
2. **Submit Paper**: Upload research papers with blockchain hash generation and AI plagiarism detection
3. **Verify Paper**: Verify paper authenticity using blockchain hashes or search functionality
4. **Research Papers**: Browse and filter academic papers in the system
5. **Dashboard**: Analytics and management interface for the platform
6. **About**: Company information, team, and platform values

## Technology Stack

- **React 18**: Latest React with hooks and modern features
- **React Router**: Client-side routing
- **CSS3**: Modern CSS with Grid, Flexbox, and animations
- **Blockchain Integration**: Hash generation and verification simulation
- **AI Integration**: Plagiarism detection simulation
- **Responsive Design**: Mobile-first approach

## Getting Started

### Prerequisites

- Node.js (version 14 or higher)
- npm or yarn package manager

### Installation

1. **Install dependencies**:
   ```bash
   npm install
   ```

2. **Start the development server**:
   ```bash
   npm start
   ```

3. **Open your browser**:
   Navigate to `http://localhost:3000` to view the application

### Available Scripts

- `npm start` - Runs the app in development mode
- `npm build` - Builds the app for production
- `npm test` - Launches the test runner
- `npm eject` - Ejects from Create React App (not recommended)

## Project Structure

```
src/
├── components/
│   ├── Header.js          # Navigation header component
│   └── Header.css         # Header styles
├── pages/
│   ├── Home.js            # Home page with hero section
│   ├── Home.css           # Home page styles
│   ├── SubmitPaper.js     # Paper submission with blockchain hash
│   ├── SubmitPaper.css    # Submit paper styles
│   ├── VerifyPaper.js     # Paper verification page
│   ├── VerifyPaper.css    # Verify paper styles
│   ├── Paper.js           # Research papers browsing
│   ├── Paper.css          # Paper browsing styles
│   ├── Dashboard.js       # Analytics dashboard
│   ├── Dashboard.css      # Dashboard styles
│   ├── About.js           # About page
│   └── About.css          # About page styles
├── App.js                 # Main app component with routing
├── App.css                # App-level styles
├── index.js               # Application entry point
└── index.css              # Global styles
```

## Key Features

### Paper Submission Process
1. **Upload Paper**: Submit research paper in PDF format
2. **Generate Hash**: Create unique blockchain hash for the paper
3. **AI Plagiarism Check**: Automated scanning against academic databases
4. **Blockchain Storage**: Store paper hash on blockchain for immutability
5. **Verification**: Get instant verification results

### Paper Verification
- **Hash-based Verification**: Verify papers using blockchain hashes
- **Title/Author Search**: Search papers by title or author name
- **Blockchain Information**: Display transaction details and block information
- **Authenticity Metrics**: Show plagiarism scores and authenticity ratings

### AI Plagiarism Detection
- **Multi-source Scanning**: Check against academic databases, published papers, and online sources
- **Similarity Scoring**: Provide detailed similarity percentages
- **Recommendations**: Get actionable recommendations based on results
- **Real-time Processing**: Instant results with detailed analysis

## Design Features

### Hero Section
- **Left Side**: Compelling text content explaining the platform
- **Right Side**: Animated blockchain visualization showing the process
- **Responsive**: Adapts beautifully to mobile devices

### Navigation
- **Fixed Header**: Sticky navigation with blur effect
- **Active States**: Visual feedback for current page
- **Mobile Menu**: Hamburger menu for mobile devices
- **Auth Buttons**: Login and Sign Up buttons on the right

### Animations
- **Smooth Transitions**: CSS transitions and transforms
- **Floating Elements**: Subtle floating animations
- **Hover Effects**: Interactive hover states
- **Loading Animations**: Fade-in effects for content

## Customization

### Colors
The application uses a consistent color scheme:
- Primary: `#667eea` (Blue)
- Secondary: `#764ba2` (Purple)
- Success: `#10b981` (Green)
- Error: `#ef4444` (Red)
- Background: Gradient from `#f5f7fa` to `#c3cfe2`

### Typography
- Font Family: Inter (Google Fonts)
- Weights: 300, 400, 500, 600, 700

### Components
All components are modular and can be easily customized by modifying their respective CSS files.

## Blockchain Integration

The platform simulates blockchain functionality including:
- Hash generation for research papers
- Block number assignment
- Transaction hash creation
- Timestamp verification
- Authenticity checking

## AI Integration

The platform simulates AI-powered plagiarism detection:
- Multi-source document comparison
- Similarity scoring algorithms
- Detailed analysis reports
- Real-time processing simulation

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Deployment

To deploy the application:

1. **Build for production**:
   ```bash
   npm run build
   ```

2. **Deploy the `build` folder** to your hosting service (Netlify, Vercel, AWS, etc.)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is open source and available under the [MIT License](LICENSE).

## Support

For support or questions, please open an issue in the repository or contact the development team.
