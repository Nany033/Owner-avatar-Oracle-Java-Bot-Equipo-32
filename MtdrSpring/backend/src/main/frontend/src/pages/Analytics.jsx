import { useState } from 'react';
import { CircularProgress, Button, Alert } from '@mui/material';


export default function Analytics() {
    const [analytics, setAnalytics] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [lastGenerated, setLastGenerated] = useState(null);

    const generateAnalytics = async () => {
        setLoading(true);
        setError(null);
        
        try {
            const response = await fetch('/analytics/generate');
            
            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }
            
            const analyticsText = await response.text();
            setAnalytics(analyticsText);
            setLastGenerated(new Date());
        } catch (err) {
            console.error('Error generating analytics:', err);
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const formatAnalytics = (text) => {
        // Convertir markdown a HTML para mejor presentación
        return text
            .replace(/## (.*)/g, '<h2 class="analytics-h2">$1</h2>')
            .replace(/### (.*)/g, '<h3 class="analytics-h3">$1</h3>')
            .replace(/\*\*(.*?)\*\*/g, '<strong class="analytics-strong">$1</strong>')
            .replace(/\*(.*?)\*/g, '<em class="analytics-em">$1</em>')
            .replace(/- (.*)/g, '<li class="analytics-li">$1</li>')
            .replace(/(\n\n)/g, '<br/><br/>')
            .replace(/\n/g, '<br/>')
            // Envolver listas consecutivas en <ul>
            .replace(/(<li class="analytics-li">.*?<\/li>)(?:\s*<li class="analytics-li">.*?<\/li>)*/g, '<ul class="analytics-ul">$&</ul>')
            .replace(/<\/li>\s*<li class="analytics-li">/g, '</li><li class="analytics-li">');
    };

    return (
        <div className="analytics-container">
            <div className="analytics-header">
                <h1>📊 Advanced Project Analytics</h1>
                <p className="analytics-subtitle">
                    AI-powered insights to optimize your development process with automatic task categorization and advanced productivity analysis
                </p>
            </div>

            <div className="analytics-controls">
                <div className="analytics-description">
                    <h3>🤖 What this analysis includes:</h3>
                    <ul>
                        <li>🏷️ <strong>Automatic Task Categorization</strong> - AI categorizes tasks into: Back-end, Front-end, Database, DevOps, Security, Testing, Quality, Documentation</li>
                        <li>⏱️ <strong>Time Analysis by Category</strong> - Real time invested per category</li>
                        <li>📈 <strong>Estimation Accuracy</strong> - Where estimated vs actual time deviated most</li>
                        <li>👥 <strong>Developer Performance by Sprint</strong> - Time distribution % per developer per sprint</li>
                        <li>🎯 <strong>Planning Improvements</strong> - Specific suggestions for next sprint planning</li>
                        <li>📋 <strong>Next Sprint Recommendations</strong> - New task proposals with MoSCoW/Scrum prioritization</li>
                    </ul>
                </div>

                <Button 
                    variant="contained" 
                    onClick={generateAnalytics}
                    disabled={loading}
                    className="generate-analytics-button"
                    size="large"
                >
                    {loading ? (
                        <>
                            <CircularProgress size={20} color="inherit" style={{ marginRight: '10px' }} />
                            Generating Analytics...
                        </>
                    ) : (
                        '🚀 Click here to generate your statistics'
                    )}
                </Button>
            </div>

            {lastGenerated && (
                <div className="last-generated">
                    <small>
                        ✅ Last generated: {lastGenerated.toLocaleString()}
                    </small>
                </div>
            )}

            <div className="analytics-content">
                {loading && (
                    <div className="loading-container">
                        <CircularProgress size={60} />
                        <p>🤖 AI is analyzing your project data...</p>
                        <p><small>This may take 10-30 seconds for comprehensive analysis</small></p>
                    </div>
                )}

                {error && (
                    <Alert severity="error" className="error-alert">
                        <strong>❌ Error generating analytics:</strong> {error}
                        <br />
                        <small>
                            Please ensure the OpenAI API key is configured correctly in the backend.
                        </small>
                    </Alert>
                )}

                {analytics && !loading && (
                    <div className="analytics-result">
                        <div className="analytics-result-header">
                            <h2>🎯 AI-Generated Project Analytics</h2>
                            <p>Comprehensive analysis powered by ChatGPT-4o</p>
                        </div>
                        
                        <div 
                            className="analytics-text"
                            dangerouslySetInnerHTML={{ 
                                __html: formatAnalytics(analytics) 
                            }}
                        />
                        
                        <div className="analytics-footer">
                            <small>
                                🤖 This analysis was generated using ChatGPT-4o based on your current project data.
                                Results include automatic task categorization and advanced productivity insights.
                            </small>
                        </div>
                    </div>
                )}

                {!analytics && !loading && !error && (
                    <div className="empty-state">
                        <div className="empty-state-icon">📈</div>
                        <h3>Ready to unlock project insights?</h3>
                        <p>
                            Click the button above to generate a comprehensive AI-powered analysis of your project, 
                            including task categorization, time analysis, and recommendations for your next sprint.
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
}