export interface SocialMedia {
  id: number;
  artistId: number;
  url: string;
}

export interface ParsedSocialMedia {
  platform: 'instagram' | 'facebook' | 'twitter' | 'youtube' | 'tiktok' | 'other';
  username: string;
  fullUrl: string;
  displayName: string;
}
